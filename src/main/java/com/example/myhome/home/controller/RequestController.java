package com.example.myhome.home.controller;

import com.example.myhome.home.model.RepairRequest;
import com.example.myhome.home.model.filter.FilterForm;
import com.example.myhome.home.repository.AdminRepository;
import com.example.myhome.home.repository.OwnerRepository;
import com.example.myhome.home.repository.RepairRequestRepository;
import com.example.myhome.home.service.AdminService;
import com.example.myhome.home.service.OwnerService;
import com.example.myhome.home.service.RepairRequestService;
import com.example.myhome.home.validator.RequestValidator;
import com.example.myhome.util.UserRole;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/requests")
@Log
public class RequestController {

    @Autowired
    private RepairRequestService repairRequestService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private AdminService adminService;

    @Autowired private RequestValidator validator;

    @GetMapping
    public String showRequestsPage(Model model,
                                   FilterForm filterForm) throws IllegalAccessException {

        if(filterForm.getPage() == null) return "redirect:/admin/requests?page=0";

        List<RepairRequest> requestList;

        requestList = repairRequestService.findAllBySpecification(filterForm);

        model.addAttribute("requests", requestList);
        model.addAttribute("owners", ownerService.findAllDTO());
        model.addAttribute("masters", adminService.findAllDTO());

        log.info(filterForm.toString());

        model.addAttribute("filter_form", filterForm);
        return "admin_panel/requests/requests";
    }

    @GetMapping("/create")
    public String showCreateRequestPage(Model model) {

        model.addAttribute("repairRequest", new RepairRequest());
        model.addAttribute("id", repairRequestService.getMaxId()+1);
        model.addAttribute("owners", ownerService.findAll());
        model.addAttribute("masters", adminService.findAll().stream()
                .filter(master -> master.getRole() != UserRole.ROLE_ADMIN &&
                        master.getRole() != UserRole.ROLE_MANAGER &&
                        master.getRole() != UserRole.ROLE_DIRECTOR)
                .sorted(Comparator.comparing(o -> o.getRole().getName()))
                .collect(Collectors.toList()));

        model.addAttribute("date", LocalDate.now());
        model.addAttribute("time", LocalTime.now());

        return "admin_panel/requests/request_card";
    }

    @GetMapping("/update/{id}")
    public String showUpdateRequestPage(@PathVariable long id, Model model) {
        RepairRequest repairRequest = repairRequestService.findRequestById(id);
        if(repairRequest.getBest_time_request() == null) repairRequest.setBest_time_request(LocalDateTime.now());
        log.info(repairRequest.toString());
        model.addAttribute("repairRequest", repairRequest);
        model.addAttribute("id", id);
        model.addAttribute("date", repairRequest.getRequest_date().toLocalDate());
        model.addAttribute("time", repairRequest.getRequest_date().toLocalTime());
        model.addAttribute("best_date", repairRequest.getBest_time_request().toLocalDate());
        model.addAttribute("best_time", repairRequest.getBest_time_request().toLocalTime());
        model.addAttribute("owners", ownerService.findAll());
        model.addAttribute("masters", adminService.findAll().stream()
                .filter(master -> master.getRole() != UserRole.ROLE_ADMIN &&
                        master.getRole() != UserRole.ROLE_MANAGER &&
                        master.getRole() != UserRole.ROLE_DIRECTOR)
                .sorted(Comparator.comparing(o -> o.getRole().getName()))
                .collect(Collectors.toList()));

        return "admin_panel/requests/request_card";
    }

    @GetMapping("/info/{id}")
    public String getRequestInfoPage(@PathVariable long id, Model model) {
        RepairRequest request = repairRequestService.findRequestById(id);
        model.addAttribute("request", request);
        model.addAttribute("request_date", request.getRequest_date().toLocalDate());
        model.addAttribute("request_time", request.getRequest_date().toLocalTime());
        return "admin_panel/requests/request_profile";
    }

    @PostMapping("/create")
    public String createRequest(@ModelAttribute RepairRequest repairRequest,
                                BindingResult bindingResult,
                                @RequestParam(required = false) String date,
                                @RequestParam(required = false) String time,
                                @RequestParam(required = false) String best_date,
                                @RequestParam(required = false) String best_time) {

        if(best_date == null || best_date.isEmpty()) best_date = LocalDate.now().plusDays(2).toString();
        if(best_time == null || best_time.isEmpty()) best_time = LocalTime.of(12, 0).toString();

        repairRequest.setRequest_date(LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time)));
        repairRequest.setBest_time_request(LocalDateTime.of(LocalDate.parse(best_date), LocalTime.parse(best_time)));

        validator.validate(repairRequest, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) return "admin_panel/requests/request_card";

        repairRequestService.saveRequest(repairRequest);
        return "redirect:/admin/requests";
    }

    @PostMapping("/update/{id}")
    public String updateRequest(@PathVariable long id,
                                @ModelAttribute RepairRequest repairRequest,
                                BindingResult bindingResult,
                                @RequestParam(required = false) String best_date,
                                @RequestParam(required = false) String best_time) {

        if(best_date == null) best_date = LocalDate.now().plusDays(2).toString();
        if(best_time == null) best_time = LocalTime.of(12, 0).toString();

        RepairRequest originalRequest = repairRequestService.findRequestById(id);
        repairRequest.setRequest_date(originalRequest.getRequest_date());
        repairRequest.setBest_time_request(LocalDateTime.of(LocalDate.parse(best_date), LocalTime.parse(best_time)));

        validator.validate(repairRequest, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) return "admin_panel/requests/request_card";

        repairRequestService.saveRequest(repairRequest);
        return "redirect:/admin/requests";
    }

    @GetMapping("/delete/{id}")
    public String deleteRequest(@PathVariable long id) {
        repairRequestService.deleteRequestById(id);
        return "redirect:/admin/requests";
    }

    @ModelAttribute
    public void addAttributes(Model model) {


    }



}
