package com.charity_management_system.service;

import com.charity_management_system.dto.CaseDTO;
import com.charity_management_system.model.Case;

import java.io.File;
import java.util.List;

public interface CaseService {

    List<CaseDTO> showCases();
    CaseDTO getCase(int caseId);
    Case createCase(CaseDTO caseDTO, String categoryName, File image);
    Case updateCase(int caseId, CaseDTO theCase, File image);
    String deleteCase(int caseId);
    List<CaseDTO> getUserCasesByUsername(String username);
    List<CaseDTO> getCasesByCategory(int categoryId);
    List<CaseDTO> searchCases(String query);
}
