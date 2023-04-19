package com.example.myhome.home.repository;
import com.example.myhome.home.model.Apartment;
import com.example.myhome.home.model.ApartmentDTO;
import com.example.myhome.home.specification.ApartmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;


public interface ApartmentRepository extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    Page<Apartment> findAll(Pageable pageable);
    List<Apartment>findApartmentsByBuildingIdAndSection(Long id, String section);
    List<ApartmentDTO> findAllProjectedBy();

    List<Apartment>findApartmentsByBalanceBefore(Double balance);
    List<Apartment>findApartmentsByBuildingId(Long id);
    List<Apartment>findApartmentsByBuildingIdAndBalanceBefore(Long id, Double balance);
    List<Apartment>findApartmentsByBuildingIdAndSectionContainingIgnoreCase(Long id, String section);
    List<Apartment>findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndBalanceBefore(Long id, String section, Double balance);
    List<Apartment>findApartmentsByBuildingIdAndFloorContainingIgnoreCase(Long id, String floor);
    List<Apartment>findApartmentsByBuildingIdAndFloorContainingIgnoreCaseAndBalanceBefore(Long id, String floor, Double balance);
    List<Apartment>findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCase(Long id, String section, String floor);
    List<Apartment>findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCaseAndBalanceBefore(Long id, String section, String floor, Double balance);
    Long countAllBy();
    Long getNumberById(long flat_id);

    Optional<Apartment> findByNumber(Long number);

    default Page<Apartment> findByFilters(Long number, String building, String section, String floor, Long ownerId, String debt, Pageable pageable) {
        System.out.println("floor" +floor+".");
        System.out.println("section " +section+".");
        System.out.println("debt " +debt+".");
        Specification<Apartment> spec = Specification.where(null);

        if (number != null) {
            spec = spec.and(ApartmentSpecification.numberContains(number));
        }

        if (building != null && !building.isEmpty() && !building.equalsIgnoreCase("-")) {
            spec = spec.and(ApartmentSpecification.buildingContains(building));
        }

        if (section != null && !section.isEmpty() && !section.equalsIgnoreCase("-")) {
            spec = spec.and(ApartmentSpecification.sectionContains(section));
        }

        if (floor != null && !floor.isEmpty() && !floor.equalsIgnoreCase("-")) {
            spec = spec.and(ApartmentSpecification.floorContains(floor));
        }

        if (ownerId != null && ownerId>0) {
            spec = spec.and(ApartmentSpecification.ownerContains(ownerId));
        }

        if (debt != null && !debt.equalsIgnoreCase("-")) {
            if( debt.equalsIgnoreCase("haveDebt")) {
            spec = spec.and(ApartmentSpecification.hasdebtContains());
            } else if ( debt.equalsIgnoreCase("noDebt")) {
                spec = spec.and(ApartmentSpecification.hasNodebtContains());
            }
        }
        return findAll(spec,pageable);
    }


}

