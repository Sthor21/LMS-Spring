package com.infosys.services;

import com.infosys.beans.BorrowRecord;
import com.infosys.dto.response.BorrowResponseDTO;
import com.infosys.dto.response.PopularBooksDTO;
import com.infosys.dto.response.RecentActivitiesResponseDTO;
import com.infosys.repository.BookRepository;
import com.infosys.repository.BorrowRecordRepository;
import com.infosys.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashBoardService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public  int countALlBooks(){
        return bookRepository.countAllBooks();
    }

    public int countAllUsers(){
        return userRepository.countAllUsers();
    }

    public int countBorrowedBooks(){
        return borrowRecordRepository.countBorrowedBooks();
    }

    public int countOverdueBooks(){
        return borrowRecordRepository.countOverdueBooks();
    }

    public List<RecentActivitiesResponseDTO> getAllBorrowRecords(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return borrowRecordRepository.findAllByOrderByBorrowDateDescIdDesc()
                .stream()
                .limit(limit)
                .map(this::mapToRecentActivitiesDTO)
                .collect(Collectors.toList());
    }

    public List<PopularBooksDTO>findAllPopularBooks(int limit){
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        return borrowRecordRepository.findMostBorrowedBooks()
            .stream()
            .limit(limit)
            .map(book -> new PopularBooksDTO(
                    book.getTitle(),
                    book.getBorrowedCopies(),
                    4.4
            ))
            .collect(Collectors.toList());

    }


//    helper map function
    private RecentActivitiesResponseDTO mapToRecentActivitiesDTO(BorrowRecord borrowRecord) {
        RecentActivitiesResponseDTO dto = new RecentActivitiesResponseDTO();
        dto.setBorrowID(borrowRecord.getId());
        dto.setBorrowStatus(borrowRecord.getStatus());
        dto.setUserName(borrowRecord.getUser().getFirstName()); // Direct access through relationship
        dto.setBookTitle(borrowRecord.getBook().getTitle());
        dto.setTime(borrowRecord.getBorrowDate());
        return dto;
    }


}
