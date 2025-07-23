package com.infosys.controller;

import com.infosys.dto.response.DashBoardResponseDTO;
import com.infosys.dto.response.PopularBooksDTO;
import com.infosys.dto.response.RecentActivitiesResponseDTO;
import com.infosys.services.DashBoardService;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/dashboard")
public class DashBoardController {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DashBoardService dashBoardService;

    @GetMapping("/statistic")
    public ResponseEntity<DashBoardResponseDTO> stats(){
        DashBoardResponseDTO dashBoardResponseDTO = new DashBoardResponseDTO();
        dashBoardResponseDTO.setTotal_Books(dashBoardService.countALlBooks());
        dashBoardResponseDTO.setActive_Members(dashBoardService.countAllUsers());
        dashBoardResponseDTO.setOverdue_Books(dashBoardService.countOverdueBooks());
        dashBoardResponseDTO.setBooks_Borrowed(dashBoardService.countBorrowedBooks());
        return ResponseEntity.ok(dashBoardResponseDTO);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RecentActivitiesResponseDTO>> getAllBorrows(){
        return ResponseEntity.ok(dashBoardService.getAllBorrowRecords(5));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularBooksDTO>>findPopularBooks(){
        return ResponseEntity.ok(dashBoardService.findAllPopularBooks(5));
    }
}
