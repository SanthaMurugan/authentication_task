package com.admin.user.admin.user.service;


import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.common.RequestMeta;
import com.admin.user.admin.user.common.StringConstants;
import com.admin.user.admin.user.dto.AddInfoDto;
import com.admin.user.admin.user.dto.UpdateDto;
import com.admin.user.admin.user.entity.User;
import com.admin.user.admin.user.jwttoken.GenerateToken;
import com.admin.user.admin.user.repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;
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

    @Transactional
    public ResponseEntity<ApiResponse> addInfo(AddInfoDto addInfoDto) throws DataAccessException{
        try {
            String username = requestMeta.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_USERNAME, new ArrayList<>()));
            }
            User user = adminRepo.loginApi(username);

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
            User user = adminRepo.findByUsername(requestMeta.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            }
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, StringConstants.DATA_RECEIVED, user));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED, StringConstants.DATA_NOT_RECEIVED + e.getMessage(), new ArrayList<>()));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.NOT_UPDATED+ e.getMessage(), new ArrayList<>()));
        }
    }


    @Transactional
    public ResponseEntity<ApiResponse> delete() {
        try {
            String username = requestMeta.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_USERNAME, new ArrayList<>()));
            }
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaDelete<User> criteriaDelete = criteriaBuilder.createCriteriaDelete(User.class);
            Root<User> root = criteriaDelete.from(User.class);

            criteriaDelete.where(criteriaBuilder.equal(root.get("username"), username));

            int deletedCount = entityManager.createQuery(criteriaDelete).executeUpdate();

            if (deletedCount > 0) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, StringConstants.DELETED_SUCCESSFULLY, new HashMap<>()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USER_NOT_FOUND, new ArrayList<>()));
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, StringConstants.FAIL_TO_DELETE + e.getMessage(), new ArrayList<>()));
        }
    }


}
