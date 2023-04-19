package com.example.myhome.home.service;

import com.example.myhome.home.model.*;
import com.example.myhome.home.model.filter.FilterForm;
import com.example.myhome.home.repository.RepairRequestRepository;
import com.example.myhome.home.repository.specifications.RequestSpecifications;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class RepairRequestService {

    @Autowired
    private RepairRequestRepository repairRequestRepository;

    @Autowired private ApartmentService apartmentService;
    @Autowired private OwnerService ownerService;
    @Autowired private AdminService adminService;

    public List<RepairRequest> findAllRequests() {return repairRequestRepository.findAll();}

    public Page<RepairRequest> findAllBySpecification(FilterForm filters) throws IllegalAccessException {

        if(!filters.filtersPresent()) return repairRequestRepository.findAll(PageRequest.of(filters.getPage()-1, 10));

        log.info("Filters found");
        log.info(filters.toString());
        Long id = filters.getId();
        String description = filters.getDescription();
        RepairMasterType masterType = (filters.getMaster_type() != null) ? RepairMasterType.valueOf(filters.getMaster_type()) : null;
        String phone = filters.getPhone();
        RepairStatus status = (filters.getStatus() != null) ? RepairStatus.valueOf(filters.getStatus()) : null;
        Apartment apartment = (filters.getApartment() != null) ? apartmentService.findByNumber(filters.getApartment()) : null;
        Owner owner = (filters.getOwner() != null) ? ownerService.findById(filters.getOwner()) : null;
        Admin master = (filters.getMaster() != null) ? adminService.findAdminById(filters.getMaster()) : null;

        String datetime = filters.getDatetime();
        LocalDateTime from = null, to = null;

        if(datetime != null && !datetime.isEmpty()) {
            String datetime_from = datetime.split(" to ")[0];
            from =
                    LocalDateTime.of(LocalDate.parse(datetime_from.split(" ")[0]),
                                    LocalTime.parse(datetime_from.split(" ")[1]));
            String datetime_to = datetime.split(" to ")[1];
            to =
                    LocalDateTime.of(LocalDate.parse(datetime_to.split(" ")[0]),
                                    LocalTime.parse(datetime_to.split(" ")[1]));
        }

        Specification<RepairRequest> specification =
                Specification.where(RequestSpecifications.hasId(id)
                                .and(RequestSpecifications.hasMasterType(masterType))
                                .and(RequestSpecifications.hasDescriptionLike(description))
                                .and(RequestSpecifications.hasApartment(apartment))
                                .and(RequestSpecifications.hasOwner(owner))
                                .and(RequestSpecifications.hasPhoneLike(phone))
                                .and(RequestSpecifications.hasMaster(master))
                                .and(RequestSpecifications.hasStatus(status))
                                .and(RequestSpecifications.datesBetween(from, to)));

        Pageable page = PageRequest.of(filters.getPage()-1,10);

        Page<RepairRequest> foundItems = repairRequestRepository.findAll(specification, page);
        log.info("Found items: " + foundItems);

        return foundItems;
    }

    public Long getMaxId() {return repairRequestRepository.getMaxId().orElse(0L);}

    public RepairRequest findRequestById(long request_id) {return repairRequestRepository.findById(request_id).orElseThrow();}

    public RepairRequest saveRequest(RepairRequest request) {return repairRequestRepository.save(request);}

    public void deleteRequestById(long request_id) {repairRequestRepository.deleteById(request_id);}

}
