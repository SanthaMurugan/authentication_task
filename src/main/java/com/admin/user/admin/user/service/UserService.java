package com.admin.user.admin.user.service;


import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.common.RequestMeta;
import com.admin.user.admin.user.common.StringConstants;
import com.admin.user.admin.user.dto.AddInfoDto;
import com.admin.user.admin.user.dto.LoginDto;
import com.admin.user.admin.user.dto.SignupDto;
import com.admin.user.admin.user.dto.UpdateDto;
import com.admin.user.admin.user.entity.User;
import com.admin.user.admin.user.exception.UserNameAlreadyExistsException;
import com.admin.user.admin.user.jwttoken.GenerateToken;
import com.admin.user.admin.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GenerateToken generateToken;
    @Autowired
    private ApiResponse apiResponse;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RequestMeta requestMeta;
    @Autowired
    private EntityManager entityManager;


    public ResponseEntity<ApiResponse> signup(SignupDto signupDto) throws Exception{
        try {
            User existingUser = userRepo.findByUsername(signupDto.getUsername());
            if (existingUser != null) {
                throw new UserNameAlreadyExistsException(StringConstants.USER_NAME_ALREADY_EXISTS);            }
            User user = new User();
            String bcrypt = bCryptPasswordEncoder.encode(signupDto.getPassword());
            user.setUsername(signupDto.getUsername());
            user.setEmailID(signupDto.getEmailID());
            user.setPassword(bcrypt);
            user.setRole(signupDto.getRole());
            userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.CREATED, StringConstants.USER_NAME_ADDED_SUCCESSFULLY,new ArrayList<>()));
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED, StringConstants.USER_NAME_NOT_ADDED,new ArrayList<>()));
        }
    }

    public ResponseEntity<ApiResponse> login(LoginDto loginDto) throws Exception{
        try {
            User user = userRepo.loginApi(loginDto.getUsername());
            if (user == null) {
                throw new UsernameNotFoundException(StringConstants.USER_NAME_NOT_EXISTS);
            }

            String role = loginDto.getRole();
            if (!("user".equals(role) || "admin".equals(role)) || !user.getRole().equals(role)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_ROLE, new ArrayList<>()));
            }
            if (bCryptPasswordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                String token = generateToken.generateTokens(loginDto);
                requestMeta.setUsername(loginDto.getUsername());
                requestMeta.setRole(user.getRole());
                HashMap<String, Object> data = new HashMap<>();
                data.put("token", token);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, StringConstants.LOGIN_SUCCESSFUL,data));

            }
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED, StringConstants.LOGIN_FAILED,new ArrayList<>()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED, StringConstants.LOGIN_FAILED,new ArrayList<>()));
    }

    @Transactional
    public ResponseEntity<ApiResponse> addInfo(AddInfoDto addInfoDto) throws DataAccessException {
        try {
            String username = requestMeta.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_USERNAME, new ArrayList<>()));
            }
            User user = userRepo.loginApi(username);

            if (user != null) {
                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
                CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
                Root<User> root = criteriaUpdate.from(User.class);

                criteriaUpdate.set(root.get("age"), addInfoDto.getAge());
                criteriaUpdate.set(root.get("gender"), addInfoDto.getGender());
                criteriaUpdate.set(root.get("address"), addInfoDto.getAddress());

                criteriaUpdate.where(criteriaBuilder.equal(root.get("username"), username));

                entityManager.createQuery(criteriaUpdate).executeUpdate();

                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, StringConstants.USER_INFO_ADDED_SUCCESSFULLY, new HashMap<>()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.USER_INFO_ADDED_FAILED, new ArrayList<>()));
        }
    }

    public ResponseEntity<ApiResponse> getInfo() {
        try {
            User user = userRepo.findByUsername(requestMeta.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            } else if (user.getAddress() == null || user.getAge() == null || user.getEmailID() == null || user.getGender() == null) {
                  return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse(HttpStatus.NO_CONTENT, StringConstants.NULL_DATA, new ArrayList<>()));

            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK, StringConstants.DATA_RECEIVED, user));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.DATA_NOT_RECEIVED+ e.getMessage(), new ArrayList<>()));
        }
    }


    @Transactional
    public ResponseEntity<ApiResponse> updateInfo(UpdateDto updateDto) {
        try {
            String username = requestMeta.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_USERNAME, new ArrayList<>()));
            }
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
            Root<User> root = criteriaUpdate.from(User.class);

            criteriaUpdate.set(root.get("age"), updateDto.getAge());
            criteriaUpdate.set(root.get("gender"), updateDto.getGender());
            criteriaUpdate.set(root.get("address"), updateDto.getAddress());
            criteriaUpdate.set(root.get("emailID"), updateDto.getEmailID());
            criteriaUpdate.where(criteriaBuilder.equal(root.get("username"), username));

            int updatedCount = entityManager.createQuery(criteriaUpdate).executeUpdate();

            if (updatedCount > 0) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, StringConstants.UPDATED_SUCCESSFULLY, new HashMap<>()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.NOT_UPDATED + e.getMessage(), new ArrayList<>()));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse> delete() {
        try {
            String username = requestMeta.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_USERNAME, new ArrayList<>()));
            }
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaDelete<User> criteriaDelete = criteriaBuilder.createCriteriaDelete(User.class);
            Root<User> root = criteriaDelete.from(User.class);
            criteriaDelete.where(criteriaBuilder.equal(root.get("username"), username));

            int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();

            if (deletedCount > 0) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK, StringConstants.DELETED_SUCCESSFULLY, new HashMap<>()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.FAIL_TO_DELETE + e.getMessage(), new ArrayList<>()));
        }
    }


    public List<User> getALL() {
        String role = requestMeta.getRole();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Predicate rolePredicate = cb.equal(root.get("role"), role);
        query.select(root).where(rolePredicate);
        return entityManager.createQuery(query).getResultList();
    }
}

