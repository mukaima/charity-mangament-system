package com.charity_management_system.service;

import com.charity_management_system.dto.CaseDto;
import com.charity_management_system.model.Case;

import java.io.File;
import java.util.List;

public interface CaseService {

    List<CaseDto> showCases();
    CaseDto getCase(int caseId);
    Case createCase(CaseDto caseDTO, String categoryName, File image);
    Case updateCase(int caseId, CaseDto theCase, File image);
    String deleteCase(int caseId);
    List<CaseDto> getUserCasesByUsername(String username);
    List<CaseDto> getCasesByCategory(int categoryId);
    List<CaseDto> searchCases(String query);
}
