package com.example.myhome.home.controller;

import com.example.myhome.home.model.*;
import com.example.myhome.home.model.filter.FilterForm;
import com.example.myhome.home.repository.OwnerRepository;
import com.example.myhome.home.service.BuildingService;
import com.example.myhome.home.service.OwnerService;
import com.example.myhome.home.validator.OwnerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/owners")
@Log
public class OwnerController {

    @Value("${upload.path}")
    private String uploadPath;
    private final OwnerService ownerService;
    private final OwnerValidator ownerValidator;
    private final OwnerRepository ownerRepository;
    private final BuildingService buildingService;

    @GetMapping("/")
    public String getOwners(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        List<BuildingDTO>buildingDTOList = new ArrayList<>();
        model.addAttribute("buildings", buildingDTOList);
        Page<Owner> ownerList = ownerService.findAll(pageable);
        model.addAttribute("owners", ownerList);
        model.addAttribute("filterForm", new FilterForm());
        return "admin_panel/owners/owners";
    }
    @PostMapping("/filter")
    public String filterApartments(Model model, @ModelAttribute FilterForm filterForm, @RequestParam(name = "id",required = false) Long id, @RequestParam(name = "ownerName",required = false) String ownerName,
                                   @RequestParam(name = "phonenumber",required = false) String phonenumber, @RequestParam(name = "email",required = false) String email,
                                   @RequestParam(name = "buildingName", required = false) String buildingName, @RequestParam(name = "apartment", required = false) Long apartment,
                                   @RequestParam(name = "localDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate, @RequestParam(name = "status",required = false) String status,
                                   @RequestParam(name = "debtSting",required = false) String debt, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) throws IOException {
        System.out.println(localDate);
        Page<Owner>ownerList = ownerRepository.findByFilters(id,ownerName,phonenumber, email, buildingName, apartment, localDate, ownerService.stringStatusConverter(status), ownerService.isHaveDebt(debt), pageable);
        System.out.println(ownerList);
        model.addAttribute("owners", ownerList);
        model.addAttribute("filterForm", filterForm);
        return "admin_panel/owners/owners";

    }

    @GetMapping("/{id}")
    public String getOwner(@PathVariable("id") Long id, Model model) {
        Owner owner = ownerService.findById(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner";
    }

    @GetMapping("/new")
    public String createOwner(Model model) {
        Owner owner = new Owner();
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner_edit";
    }
    @GetMapping("edit/{id}")
    public String editeOwner(@PathVariable("id") Long id, Model model) {
        Owner owner = ownerService.findById(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner_edit";
    }

    @PostMapping("/save")
    public String saveCoffee(@Valid @ModelAttribute("owner") Owner owner, BindingResult bindingResult, @RequestParam("img1") MultipartFile file) throws IOException {
        ownerValidator.validate(owner, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin_panel/owners/owner_edit";
        } else {
            owner.setProfile_picture(ownerService.saveOwnerImage(owner.getId(), file));
            ownerService.save(owner);
        }
        return "redirect:/admin/owners/";
    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        Owner owner = ownerService.findById(id);
        try {
            Files.deleteIfExists(Path.of(uploadPath + owner.getProfile_picture()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ownerService.deleteById(id);
        return "redirect:/admin/owners/";
    }

    //Получить квартиры какого-то владельца
    @GetMapping("/get-apartments/{id}")
    public @ResponseBody List<Apartment> getOwnerApartments(@PathVariable long id) {
        return ownerService.findById(id).getApartments();
    }

    @GetMapping("/get-apartment-accounts")
    public @ResponseBody List<Long> getOwnerApartmentAccountsIds(@RequestParam long owner_id) {
        return ownerService.getOwnerApartmentAccountsIds(owner_id);
    }

    @GetMapping("/newMessage")
    public String createMessage(Model model) {
        Message message = new Message();
        model.addAttribute("message", message);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        model.addAttribute("selectWithDebt", "selectWithDebt");
        return "admin_panel/messages/message_edit";
    }
}
