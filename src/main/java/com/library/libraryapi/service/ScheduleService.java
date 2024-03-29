package com.library.libraryapi.service;

import com.library.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateLoans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateloans = loanService.getAllLateLoans();
        List<String> mailsList = allLateloans.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        emailService.sendEmails(message, mailsList);
    }
}
