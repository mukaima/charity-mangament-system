package com.charity_management_system.service.impl;

import com.charity_management_system.constant.ApplicationConstants;
import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.dto.ImageSavingResponse;
import com.charity_management_system.enums.CaseStatus;
import com.charity_management_system.model.Case;
import com.charity_management_system.model.Category;
import com.charity_management_system.model.User;
import com.charity_management_system.repository.CaseRepository;
import com.charity_management_system.repository.CategoryRepository;
import com.charity_management_system.repository.UserRepository;
import com.charity_management_system.service.CaseService;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Drive drive;
    private final CommonService commonService;

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    /**
     * Retrieves a list of all cases.
     *
     * @return A list of CaseDto objects representing the cases.
     */
    @Override
    public List<CaseDto> showCases() {
        List<Case> cases = caseRepository.findAll();

        List<CaseDto> caseDtos = new ArrayList<>();
        cases.forEach(c -> log.info(String.format("Case ID: %d" , c.getId())));

        for (Case c : cases){
            CaseDto caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDtos.add(caseDTO);
        }

        return caseDtos;
    }

    private Case getCaseOrThrow(int caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));
    }

    /**
     * Retrieves a case by its ID.
     *
     * @param caseId The ID of the case to retrieve.
     * @return A CaseDto object representing the case.
     */
    @Override
    public CaseDto getCase(int caseId) {
         Case userCase = getCaseOrThrow(caseId);
        return new CaseDto(userCase.getId(), userCase.getTitle(), userCase.getDescription(), userCase.getImagePath(), userCase.getGoal(), userCase.getAmountRaised());
    }

    /**
     * Creates a new case with the specified details and uploads an image to Google Drive.
     *
     * @param caseDTO      The case details.
     * @param categoryName The category of the case.
     * @param image        The image file to upload.
     * @return The created Case entity.
     */
    @Override
    public Case createCase(CaseDto caseDTO, String categoryName, File image) {

        ImageSavingResponse res = processingSavingImagesToDrive(image);

        Case userCase = new Case();

        userCase.setGoal(caseDTO.getGoal());
        userCase.setTitle(caseDTO.getTitle());
        userCase.setDescription(caseDTO.getDescription());
        userCase.setImagePath(res.getUrl());
        userCase.setCaseStatus(CaseStatus.APPROVED);
        userCase.setAmountRaised(0);

        // fetching the currently authenticated user


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching user for username: {}", username);
        User caseUser = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found"));
        userCase.setUser(caseUser);

        Category category = categoryRepository.findByName(categoryName);
        category.getCaseList().add(userCase);
        userCase.setCategory(category);


        return caseRepository.save(userCase);
    }

    /**
     * Updates an existing case with the specified details and uploads an image to Google Drive if provided.
     *
     * @param caseId The ID of the case to update.
     * @param theCase The updated case details.
     * @param image   The new image file to upload (optional).
     * @return The updated Case entity.
     */
    @Override
    public Case updateCase(int caseId, CaseDto theCase, File image) {
        Case targetedCase = getCaseOrThrow(caseId);
        targetedCase.setTitle(theCase.getTitle());
        targetedCase.setDescription(theCase.getDescription());
        targetedCase.setGoal(theCase.getGoal());
        if (image != null){
            ImageSavingResponse res = processingSavingImagesToDrive(image);
            targetedCase.setImagePath(res.getUrl());
        }
        return caseRepository.save(targetedCase);
    }

    /**
     * Handles uploading images to Google Drive.
     *
     * @param file The image file to upload.
     * @return An ImageSavingResponse containing the status and URL of the uploaded image.
     */
    private ImageSavingResponse processingSavingImagesToDrive(File file){
        ImageSavingResponse res = new ImageSavingResponse();
        try {
            String folderId = ApplicationConstants.FOLDER_ID;
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());
            fileMetaData.setParents(Collections.singletonList(folderId));
            FileContent fileContent = new FileContent("image/jpeg", file);
            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, fileContent)
                    .setFields("id").execute();

            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            drive.permissions().create(uploadedFile.getId(), permission).execute();

            String imageUrl = "https://drive.google.com/thumbnail?id="+uploadedFile.getId()+"&sz=w1000";
            log.info("Image uploaded to Google Drive. URL: {}", imageUrl);
            file.delete();
            res.setStatus(200);
            res.setMessage("image uploaded successfully to drive");
            res.setUrl(imageUrl);

        }catch (Exception e){
            log.debug(e.getMessage());
            res.setStatus(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }

    /**
     * Deletes a case by its ID.
     *
     * @param caseId The ID of the case to delete.
     * @return A message indicating whether the case was deleted successfully.
     */
    @Override
    public String deleteCase(int caseId) {
        if (caseRepository.existsById(caseId)){
            caseRepository.deleteById(caseId);
            return "case deleted successfully";
        }
        return "case not found";
    }

    /**
     * Retrieves all cases created by a specific user.
     *
     * @param username The username of the user whose cases to retrieve.
     * @return A list of CaseDto objects representing the user's cases.
     */
    @Override
    public List<CaseDto> getUserCasesByUsername(String username) {

        List<Case> cases = caseRepository.findAllByUserUsername(username);

        List<CaseDto> caseDtos = new ArrayList<>();


        for (Case c : cases){
            CaseDto caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDtos.add(caseDTO);
        }


        return caseDtos;
    }

    /**
     * Retrieves all cases in a specific category.
     *
     * @param categoryId The ID of the category.
     * @return A list of CaseDto objects representing the cases in the category.
     */
    @Override
    public List<CaseDto> getCasesByCategory(int categoryId) {
        List<Case> cases = caseRepository.findAllByCategoryId(categoryId);
        List<CaseDto> caseDtos = new ArrayList<>();

        for (Case c: cases){
            CaseDto caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDtos.add(caseDTO);
        }

        return caseDtos;
    }

    /**
     * Searches for cases by their title or description.
     *
     * @param query The search query.
     * @return A list of CaseDto objects matching the query.
     */
    @Override
    public List<CaseDto> searchCases(String query) {
        List<Case> cases = caseRepository.findByTitleContainingOrDescriptionContaining(query, query);
        List<CaseDto> caseDtos = new ArrayList<>();

        for (Case c: cases){
            CaseDto caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDtos.add(caseDTO);
        }

        return caseDtos;
    }
}
