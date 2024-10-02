package com.charity_management_system.controller;

import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.model.Case;
import com.charity_management_system.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    /**
     * Fetches a case by its ID.
     *
     * @param caseId The ID of the case.
     * @return The details of the case.
     */
    @GetMapping("/id")
    public ResponseEntity<CaseDto> getCase(@RequestParam("caseId") int caseId){
        return ResponseEntity.ok(caseService.getCase(caseId));
    }

    /**
     * Searches for cases by a search query.
     *
     * @param query The search query.
     * @return A list of cases matching the query.
     */
    @GetMapping("/search")
    public List<CaseDto> searchCases(@RequestParam String query) {
        return caseService.searchCases(query);
    }

    /**
     * Fetches all cases created by a user.
     *
     * @param username The username of the user.
     * @return A list of cases created by the user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<CaseDto>> getUserCaseByUsername(@RequestParam String username){
        return ResponseEntity.ok(caseService.getUserCasesByUsername(username));
    }

    /**
     * Fetches all cases under a specific category.
     *
     * @param categoryId The ID of the category.
     * @return A list of cases under the specified category.
     */
    @GetMapping("/getByCategory")
    public ResponseEntity<List<CaseDto>> getCasesByCategory(@RequestParam int categoryId){
        return ResponseEntity.ok(caseService.getCasesByCategory(categoryId));
    }

    /**
     * Creates a new case.
     *
     * @param userCase The details of the case.
     * @param categoryName The category of the case.
     * @param file The image file for the case.
     * @return A success message.
     * @throws IOException If an error occurs while saving the image.
     */
    @PostMapping("/createCase")
    public ResponseEntity<String> createCase(@RequestBody CaseDto userCase, @RequestParam String categoryName, @RequestParam("image") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        ResponseEntity.ok(caseService.createCase(userCase, categoryName, tempFile));
        return ResponseEntity.ok("done creating case");
    }

    /**
     * Fetches all cases.
     *
     * @return A list of all cases.
     */
    @GetMapping("/showCases")
    public ResponseEntity<List<CaseDto>> showCases(){
        return ResponseEntity.ok(caseService.showCases());
    }

    /**
     * Updates an existing case.
     *
     * @param caseId The ID of the case to update.
     * @param userCase The updated case details.
     * @param file The updated image file (optional).
     * @return The updated case.
     * @throws IOException If an error occurs while saving the image.
     */
    @PutMapping("/updateCase")
    public ResponseEntity<Case> updateCase(@RequestParam int caseId, @ModelAttribute CaseDto userCase,
                                           @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tmep", null);
        file.transferTo(tempFile);
        return ResponseEntity.ok(caseService.updateCase( caseId , userCase, tempFile));
    }


    /**
     * Deletes a case.
     *
     * @param caseId The ID of the case to delete.
     * @return A success message.
     */
    @DeleteMapping("/deleteCase")
    public ResponseEntity<String> deleteCase(@RequestParam int caseId){
        return  ResponseEntity.ok(caseService.deleteCase(caseId));
    }
}
