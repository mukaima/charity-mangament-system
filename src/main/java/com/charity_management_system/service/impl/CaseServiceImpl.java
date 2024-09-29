package com.charity_management_system.service.impl;

import com.charity_management_system.dto.CaseDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Drive drive;
    private final CommonService commonService;



    @Override
    public List<CaseDTO> showCases() {
        List<Case> cases = caseRepository.findAll();

        List<CaseDTO> caseDTOS = new ArrayList<>();
        cases.forEach(c -> System.out.println("Case ID: " + c.getId()));

        for (Case c : cases){
            CaseDTO caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDTOS.add(caseDTO);
        }

        return caseDTOS;
    }

    @Override
    public CaseDTO getCase(int caseId) {
         Case userCase = caseRepository.findById(caseId).orElseThrow(() -> new RuntimeException("case not found"));
        return new CaseDTO(userCase.getId(), userCase.getTitle(), userCase.getDescription(), userCase.getImagePath(), userCase.getGoal(), userCase.getAmountRaised());
    }


    @Override
    public Case createCase(CaseDTO caseDTO, String categoryName, File image) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("In createCase: " + authentication);

        ImageSavingResponse res = processingSavingImagesToDrive(image);

        Case userCase = new Case();

        userCase.setGoal(caseDTO.getGoal());
        userCase.setTitle(caseDTO.getTitle());
        userCase.setDescription(caseDTO.getDescription());
        userCase.setImagePath(res.getUrl());
        userCase.setCaseStatus(CaseStatus.approved);
        userCase.setAmountRaised(0);

        // fetching the currently authenticated user


        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User caseUser = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found"));
        userCase.setUser(caseUser);

        Category category = categoryRepository.findByName(categoryName);
        category.getCaseList().add(userCase);
        userCase.setCategory(category);


        return caseRepository.save(userCase);
    }

    @Override
    public Case updateCase(int caseId, CaseDTO theCase, File image) {
        Case targetedCase = caseRepository.findById(caseId).orElseThrow(() -> new RuntimeException("case not found"));
        targetedCase.setTitle(theCase.getTitle());
        targetedCase.setDescription(theCase.getDescription());
        targetedCase.setGoal(theCase.getGoal());
        if (image != null){
            ImageSavingResponse res = processingSavingImagesToDrive(image);
            targetedCase.setImagePath(res.getUrl());
        }
        return caseRepository.save(targetedCase);
    }

    private ImageSavingResponse processingSavingImagesToDrive(File file){
        ImageSavingResponse res = new ImageSavingResponse();
        try {
            String folderId = "1bHUGc5hJi3Qrr2oNIdb8Osg4e9iWUWFY";
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

            String imageUrl = "https://drive.google.com/thumbnail?id="+uploadedFile.getId()+"&sz=4000";
            System.out.println("image url -> " + imageUrl);
            file.delete();
            res.setStatus(200);
            res.setMessage("image uploaded successfully to drive");
            res.setUrl(imageUrl);

        }catch (Exception e){
            System.out.println(e.getMessage());
            res.setStatus(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }


    @Override
    public String deleteCase(int caseId) {
        if (caseRepository.existsById(caseId)){
            caseRepository.deleteById(caseId);
            return "case deleted successfully";
        }
        return "case not found";
    }

    @Override
    public List<CaseDTO> getUserCasesByUsername(String username) {

        List<Case> cases = caseRepository.findAllByUserUsername(username);

        List<CaseDTO> caseDTOS = new ArrayList<>();


        for (Case c : cases){
            CaseDTO caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDTOS.add(caseDTO);
        }


        return caseDTOS;
    }

    @Override
    public List<CaseDTO> getCasesByCategory(int categoryId) {
        List<Case> cases = caseRepository.findAllByCategoryId(categoryId);
        List<CaseDTO> caseDTOS = new ArrayList<>();

        for (Case c: cases){
            CaseDTO caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDTOS.add(caseDTO);
        }

        return caseDTOS;
    }

    @Override
    public List<CaseDTO> searchCases(String query) {
        List<Case> cases = caseRepository.findByTitleContainingOrDescriptionContaining(query, query);
        List<CaseDTO> caseDTOS = new ArrayList<>();

        for (Case c: cases){
            CaseDTO caseDTO = commonService.convertCaseToCaseDTO(c);
            caseDTOS.add(caseDTO);
        }

        return caseDTOS;
    }
}
