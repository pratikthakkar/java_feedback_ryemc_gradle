package com.example.feedbackapp.controller;

import com.example.feedbackapp.model.Feedback;
import com.example.feedbackapp.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FeedbackService feedbackService;

    @Autowired
    public AdminController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("averageRating", feedbackService.getAverageRating());
        model.addAttribute("countByCategory", feedbackService.getFeedbackCountByCategory());
        model.addAttribute("countByDay", feedbackService.getFeedbackCountByDay());
        return "admin_dashboard";
    }

    @GetMapping("/export")
    @ResponseBody
    public void exportCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=feedback.csv");
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        PrintWriter writer = response.getWriter();
        writer.println("ID,Text,Rating,Category,Email,SubmittedAt");
        for (Feedback f : feedbackList) {
            writer.printf("%d,\"%s\",%d,%s,%s,%s%n",
                    f.getId(),
                    f.getText().replace("\"", "\"\""),
                    f.getRating(),
                    f.getCategory(),
                    f.getEmail() == null ? "" : f.getEmail(),
                    f.getSubmittedAt().toString());
        }
        writer.flush();
        writer.close();
    }
}
