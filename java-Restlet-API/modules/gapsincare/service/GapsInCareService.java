package api.modules.gapsincare.service;

import api.modules.gapsincare.model.GapInCareActionInfo;
import api.modules.gapsincare.model.GapInCareNamesInfo;
import api.modules.gapsincare.model.GapsInCareListingInfo;
import core.domain.value.GapInCareStatuses;
import core.model.ctc.GapInCareTypes;
import core.model.ctc.GapsInCare;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by bloomd on 11/1/16.
 */
public class GapsInCareService {

  private final GapsInCareDAService gapsInCareDAService;

  private List<Integer> accountEntityIdsForNameLookup = new ArrayList<Integer>();

  @Inject
  public GapsInCareService(GapsInCareDAService gapsInCareDAService) {
    this.gapsInCareDAService = gapsInCareDAService;
  }


  /**
   * Core logic for processing gaps in care data and associated actions
   *
   * @param gapsInCare
   * @param gapInCareTypes
   * @return List<GapsInCareListingInfo>
   */
  public List<GapsInCareListingInfo> processGapsInCare(List<core.model.ctc.GapsInCare> gapsInCare, List<GapInCareTypes> gapInCareTypes) {
    // a collection of gaps in care
    List<GapsInCare> patientGapsInCare = new ArrayList<GapsInCare>();
    List<GapInCareTypes> notApplicableGapTypes = new ArrayList<GapInCareTypes>();

    // gap in care statuses we care about
    GapInCareStatuses[] gapInCareStatusesList = {GapInCareStatuses.OPEN, GapInCareStatuses.CLOSED};

    for (GapInCareTypes gapInCareType : gapInCareTypes) {
      boolean notApplicableGapType = true;

      for (GapsInCare gapInCare : gapsInCare) {
        //are the gap in care types relevant to this account and in open/closed status
        if (gapInCareType.getId().equals(gapInCare.getGapInCareTypeId().getId())
            && Arrays.stream(gapInCareStatusesList).anyMatch(gapInCare.getGapInCareStatusId()::equals)) {
          patientGapsInCare = addGapToPatientListing(patientGapsInCare, gapInCare);
          notApplicableGapType = false;
        }
      }

      if (notApplicableGapType) {
        notApplicableGapTypes.add(gapInCareType);
      }
    }

    List<GapsInCareListingInfo> gapsInCareListingInfo = addActions(patientGapsInCare);

    //lookup names
    gapsInCareListingInfo = addNames(gapsInCareListingInfo);

    gapsInCareListingInfo = sortGaps(gapsInCareListingInfo);

    // add user friendly names of all gaps
    gapsInCareListingInfo = addGapNames(gapsInCareListingInfo, notApplicableGapTypes);

    return gapsInCareListingInfo;
  }

  /**
   * Add gap names to thhe data
   *
   * @param gapsInCareListingInfo
   * @param notApplicableGapInCareTypes
   * @return List<GapsInCareListingInfo>
   */
  private List<GapsInCareListingInfo> addGapNames(List<GapsInCareListingInfo> gapsInCareListingInfo, List<GapInCareTypes> notApplicableGapInCareTypes) {

    notApplicableGapInCareTypes.forEach(gapType->{
      GapsInCareListingInfo gapInCareListing = new GapsInCareListingInfo();

      gapInCareListing.setGapInCareType(gapType);
      gapsInCareListingInfo.add(gapInCareListing);
    });

    return gapsInCareListingInfo;
  }

    /**
     * Add user readable names for any accountEntityIds used in the gaps or actions
     *
     * @param gapsInCareListingInfo
     *
     * @return List<GapsInCareListingInfo>
     */
  private List<GapsInCareListingInfo> addNames(List<GapsInCareListingInfo> gapsInCareListingInfo) {
    List<GapInCareNamesInfo> names = gapsInCareDAService.getNames(accountEntityIdsForNameLookup);
    HashMap namesMap = new HashMap();
    names.forEach(name->{
      namesMap.put(name.getAccountEntityId(), name.getFirstName() + " " + name.getLastName());
    });

    gapsInCareListingInfo.forEach(gicli -> {
      gicli.setModifiedByName(namesMap.get(gicli.getGapInCare().getCreatedBy()).toString());
        gicli.getGapInCareActions().forEach(
            gica -> {
              gica.setModifiedByName(namesMap.get(gica.getCreatedBy()).toString());
            }
        );
    });

    return gapsInCareListingInfo;

  }


  /**
   * Add a gap to patient's list of gaps if new gap is more recent or a new gap type
   *
   * @param patientGapsInCare
   * @param gapInCare
   *
   * @return List<GapsInCare>
   */
  private List<GapsInCare> addGapToPatientListing(List<GapsInCare> patientGapsInCare, GapsInCare gapInCare) {

    // is this gap in care type already on the patients list
    Optional existingGapInCare = patientGapsInCare.stream().filter(g -> g.getGapInCareTypeId().getId().equals(gapInCare.getGapInCareTypeId().getId())).findFirst();

    if (existingGapInCare.isPresent()) {

      // if existing gap is older, replace it.
      Object existingGapObject = existingGapInCare.get();
      GapsInCare existingGap = ((GapsInCare) existingGapObject);

      if (existingGap.getModified().before(gapInCare.getModified())) {
        Predicate<GapsInCare> gapsInCarePredicate = g -> g.getModified().before(gapInCare.getModified()) && g.getGapInCareTypeId().equals(gapInCare.getGapInCareTypeId());

        patientGapsInCare.removeIf(gapsInCarePredicate);

        accountEntityIdsForNameLookup.add(gapInCare.getCreatedBy());
        patientGapsInCare.add(gapInCare);
      }
    } else {
      accountEntityIdsForNameLookup.add(gapInCare.getCreatedBy());
      patientGapsInCare.add(gapInCare);
    }

    return patientGapsInCare;
  }


  /**
   * Add associated actions to gaps in care
   *
   * @param patientGapsInCare
   *
   * @return List<GapsInCareListingInfo>
   */
  private List<GapsInCareListingInfo> addActions(List<GapsInCare> patientGapsInCare) {

    List<Integer> actionLookupGapInCareIds = patientGapsInCare.stream().map(GapsInCare::getId).collect(Collectors.toList());
    List<GapInCareActionInfo> actions = gapsInCareDAService.getActions(actionLookupGapInCareIds);

    accountEntityIdsForNameLookup.addAll(actions.stream().map(GapInCareActionInfo::getCreatedBy).collect(Collectors.toList()));

    List<GapsInCareListingInfo> gapsInCareListingInfo = new ArrayList<GapsInCareListingInfo>();

    for (GapsInCare patientGapInCare : patientGapsInCare) {
      GapsInCareListingInfo gapInCareListing = new GapsInCareListingInfo();
      gapInCareListing.setGapInCare(patientGapInCare);

      // attach any actions associated with this gap
      List<GapInCareActionInfo> actionsForGap = actions.stream().filter(
          gapInCareActionInfo -> gapInCareActionInfo.getGapInCareId().equals(patientGapInCare.getId())).collect(Collectors.toList()
      );

      gapInCareListing.setGapInCareActions(actionsForGap);
      gapsInCareListingInfo.add(gapInCareListing);
    }

    return gapsInCareListingInfo;

  }

  /**
   * Sort the gaps
   *
   * @param gapsInCareListingInfo
   * @return List<GapsInCareListingInfo>
   */
  private List<GapsInCareListingInfo> sortGaps(List<GapsInCareListingInfo> gapsInCareListingInfo) {
    // sort by status
    Comparator<GapsInCareListingInfo> byStatus =
        (g1, g2) -> g1.getGapInCare().getGapInCareStatusId().compareTo(g2.getGapInCare().getGapInCareStatusId());
    Comparator<GapsInCareListingInfo> byDate =
        (g1, g2) -> g1.getGapInCare().getModified().compareTo(g2.getGapInCare().getModified());

    gapsInCareListingInfo = gapsInCareListingInfo.stream().sorted(byStatus.thenComparing(byDate)).collect(Collectors.toList());

    return gapsInCareListingInfo;

  }

}
