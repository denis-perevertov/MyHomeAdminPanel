package com.example.myhome.home.validator;

import com.example.myhome.home.dto.ApartmentAccountDTO;
import com.example.myhome.home.model.ApartmentAccount_;
import com.example.myhome.home.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AccountValidator implements Validator {

    @Autowired private AccountServiceImpl accountServiceImpl;
    @Autowired private MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return ApartmentAccountDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ApartmentAccountDTO account = (ApartmentAccountDTO) target;

        if(account.getBuilding() == null || account.getBuilding().getId() == null) {
            System.out.println("Error found(building)");
            errors.rejectValue(ApartmentAccount_.BUILDING, "building.empty", messageSource.getMessage("accounts.building.empty", null, LocaleContextHolder.getLocale()));
        } else if(account.getSection() == null || account.getSection().equalsIgnoreCase("0")) {
            System.out.println("Error found(section)");
            errors.rejectValue(ApartmentAccount_.SECTION, "section.empty", messageSource.getMessage("accounts.section.empty", null, LocaleContextHolder.getLocale()));
        } else if(account.getApartment() == null || account.getApartment().getId() == 0) {
            System.out.println("Error found(apartment)");
            errors.rejectValue(ApartmentAccount_.APARTMENT, "apartment.empty", messageSource.getMessage("accounts.apartment.empty", null, LocaleContextHolder.getLocale()));
        } else if(accountServiceImpl.apartmentHasAccount(account.getApartment().getId())) {
            if(account.getChangedState() == null || !account.getChangedState()) errors.rejectValue(ApartmentAccount_.APARTMENT, "apartment.has_account", messageSource.getMessage("accounts.apartment.has_account", null, LocaleContextHolder.getLocale()));
        }
        if(account.getIsActive() == null) {
            System.out.println("Error found(active)");
            errors.rejectValue(ApartmentAccount_.IS_ACTIVE, "isActive.empty", messageSource.getMessage("accounts.isActive.empty", null, LocaleContextHolder.getLocale()));
        }
    }
}
