package com.example.myhome.home.controller;

import com.example.myhome.home.model.Service;
import com.example.myhome.home.model.Tariff;
import com.example.myhome.home.service.impl.ServiceServiceImpl;
import com.example.myhome.home.service.TariffService;
import com.example.myhome.home.validator.TariffValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/admin/tariffs")
@RequiredArgsConstructor
@Log
public class TariffController {

    private final ServiceServiceImpl serviceService;
    private final TariffService tariffService;
    private final TariffValidator validator;

    // Открыть страничку с таблицей всех тарифов
    @GetMapping
    public String showTariffsPage(@RequestParam(required = false) String sort, Model model) {
        List<Tariff> tariffs = new ArrayList<>();
        if(sort != null) {
            if(sort.equalsIgnoreCase("asc")) {
                tariffs = tariffService.findAllTariffsSorted(true);
                model.addAttribute("sort", "desc");
            }
            else if(sort.equalsIgnoreCase("desc")) {
                tariffs = tariffService.findAllTariffsSorted(false);
                model.addAttribute("sort", "asc");
            }
        } else {
            tariffs = tariffService.findAllTariffs();
            model.addAttribute("sort", "asc");
        }
        model.addAttribute("tariffs", tariffs);
        return "admin_panel/system_settings/settings_tariffs";
    }

    // Открыть страничку профиля конкретного тарифа
    @GetMapping("/{id}")
    public String showTariffInfoPage(@PathVariable long id, Model model) {
        model.addAttribute("tariff", tariffService.findTariffById(id));
        return "admin_panel/system_settings/tariff_profile";
    }

    // Открыть страничку создания нового тарифа
    @GetMapping("/create")
    public String showCreateTariffCard(Model model){
        model.addAttribute("tariff", new Tariff());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("units", serviceService.findAllUnits());
        return "admin_panel/system_settings/tariff_card";
    }

    // Открыть табличку обновления тарифа
    @GetMapping("/update/{id}")
    public String showUpdateTariffPage(@PathVariable long id, Model model) {
        Tariff tariff = tariffService.findTariffById(id);
        model.addAttribute("tariff", tariff);
        model.addAttribute("components", tariff.getComponents().entrySet());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("units", serviceService.findAllUnits());
        return "admin_panel/system_settings/tariff_card";
    }

    // Сохранить созданный тариф
    @PostMapping("/create")
    public String createTariff(@ModelAttribute Tariff tariff,
                               BindingResult bindingResult,
                               @RequestParam(required = false) String[] service_names,
                               @RequestParam(required = false) String[] prices,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        tariff.setDate(LocalDateTime.now());
        tariff.setComponents(tariffService.buildComponentsMap(service_names, prices));

        validator.validate(tariff, bindingResult);
        if(bindingResult.hasErrors()) {
            log.info("errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("services", serviceService.findAllServices());
            model.addAttribute("units", serviceService.findAllUnits());
            return "admin_panel/system_settings/tariff_card";
        }

        log.info(tariff.toString());

        try {
            tariffService.saveTariff(tariff);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("fail", e.getMessage());
            return "redirect:/admin/tariffs/create";
        }

        return "redirect:/admin/tariffs";
    }

    // Сохранить обновленный тариф
    @PostMapping("/update/{id}")
    public String updateTariff(@PathVariable long id,
                               @ModelAttribute Tariff tariff,
                               BindingResult bindingResult,
                               @RequestParam(defaultValue = "0", required = false) String[] service_names,
                               @RequestParam(defaultValue = "0", required = false) String[] prices,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        tariff.setId(id);
        tariff.setDate(LocalDateTime.now());
        tariff.setComponents(tariffService.buildComponentsMap(service_names, prices));

        validator.validate(tariff, bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("services", serviceService.findAllServices());
            model.addAttribute("units", serviceService.findAllUnits());
            return "admin_panel/system_settings/tariff_card";
        }

        log.info(tariff.toString());

        try {
            tariffService.saveTariff(tariff);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("fail", e.getMessage());
            return "redirect:/admin/tariffs/update/" +id;
        }

        return "redirect:/admin/tariffs";
    }

    // Удалить тариф по его ID
    @GetMapping("/delete/{id}")
    public String deleteTariff(@PathVariable long id) {
        tariffService.deleteTariffById(id);
        return "redirect:/admin/tariffs";
    }

    // Получать все компоненты тарифа (услуги + цены)
    @GetMapping("/get-components")
    public @ResponseBody Map<String, Double> getComponents(@RequestParam long tariff_id) throws JsonProcessingException {
        Map<Service, Double> components = tariffService.findTariffById(tariff_id).getComponents();
        Map<String, Double> comp_2 = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for(Map.Entry<Service, Double> entry : components.entrySet()) {
            comp_2.put(mapper.writeValueAsString(entry.getKey()), entry.getValue());
        }
        return comp_2;
    }

}
