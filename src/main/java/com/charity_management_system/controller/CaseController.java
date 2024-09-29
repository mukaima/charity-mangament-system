package com.charity_management_system.controller;

import com.charity_management_system.dto.CaseDTO;
import com.charity_management_system.model.Case;
import com.charity_management_system.service.CaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping("/id")
    public ResponseEntity<CaseDTO> getCase(@RequestParam("caseId") int caseId){
        return ResponseEntity.ok(caseService.getCase(caseId));
    }

    @GetMapping("/search")
    public List<CaseDTO> searchCases(@RequestParam String query) {
        return caseService.searchCases(query);
    }

    @GetMapping("/me")
    public ResponseEntity<List<CaseDTO>> getUserCaseByUsername(@RequestParam String username){
        return ResponseEntity.ok(caseService.getUserCasesByUsername(username));
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<List<CaseDTO>> getCasesByCategory(@RequestParam int categoryId){
        return ResponseEntity.ok(caseService.getCasesByCategory(categoryId));
    }

    @PostMapping("/createCase")
    public ResponseEntity<String> createCase(@RequestPart("userCase") String userCaseJson, @RequestParam String categoryName, @RequestPart("image") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        ObjectMapper objectMapper = new ObjectMapper();
        CaseDTO userCase = objectMapper.readValue(userCaseJson, CaseDTO.class);
        ResponseEntity.ok(caseService.createCase(userCase, categoryName, tempFile));
        return ResponseEntity.ok("done creating case");
    }

    @GetMapping("/showCases")
    public ResponseEntity<List<CaseDTO>> showCases(){
        return ResponseEntity.ok(caseService.showCases());
    }

    @PutMapping("/updateCase")
    public ResponseEntity<Case> updateCase(@RequestParam int caseId, @ModelAttribute CaseDTO userCase,
                                           @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tmep", null);
        file.transferTo(tempFile);
        return ResponseEntity.ok(caseService.updateCase( caseId , userCase, tempFile));
    }

    @DeleteMapping("/deleteCase")
    public ResponseEntity<String> deleteCase(@RequestParam int caseId){
        return  ResponseEntity.ok(caseService.deleteCase(caseId));
    }
}
