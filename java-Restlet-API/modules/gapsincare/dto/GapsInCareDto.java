package api.modules.gapsincare.dto;

import api.infrastructure.auditlog.AuditModelName;
import api.infrastructure.auditlog.DtoAuditLogging;
import api.modules.gapsincare.model.GapInCareActionInfo;
import api.modules.gapsincare.model.GapsInCareListingInfo;
import core.domain.value.GapInCareStatuses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bloomd on 10/27/16.
 */
public class GapsInCareDto extends DtoAuditLogging
{

  Integer gapInCareId;
  String gapName;
  String gapStatus;
  List<GapInCareActionsDto> gapInCareActions;

  public GapsInCareDto() {
    _auditModelName = AuditModelName.GAPS_IN_CARE;
    _auditIdColumn = "gapInCareId";
  }

  /**
   * Converts the gapsincare listing model to a dto object
   *
   * The method logic handles three distinct types of gaps in care
   * 1) a gap in care type that is 'not applicable' for this patient
   * 2) a gap in care type that is applicable, but does not have any actions
   * 3) a gap in care type that is applicable, with associated actions
   *
   * @param gapsInCare
   * @return GapsInCareDto
   */
  public static GapsInCareDto convert(GapsInCareListingInfo gapsInCare) {

    GapsInCareDto dto = new GapsInCareDto();
    GapInCareActionsDto gapInCareActionsDto = new GapInCareActionsDto();
    List<GapInCareActionsDto> gapInCareActions = new ArrayList<GapInCareActionsDto>();

    if  (gapsInCare.getGapInCare() != null) {
      dto.setGapInCareId(gapsInCare.getGapInCare().getId());
    }

    if (gapsInCare.getGapInCare() == null) {
      dto.setGapName(gapsInCare.getGapInCareType().getName());
      dto.setGapStatus(GapInCareStatuses.NOTAPPLICABLE.getFriendlyId());
    } else {
      dto.setGapName(gapsInCare.getGapInCare().getGapInCareTypeId().getName());
      dto.setGapStatus(gapsInCare.getGapInCare().getGapInCareStatusId().getFriendlyId());

      List<GapInCareActionInfo> gapInCareActionsToAdd = gapsInCare.getGapInCareActions();

      if (gapInCareActionsToAdd != null) {
        gapInCareActionsToAdd.forEach(gica ->{
          gapInCareActions.add(gapInCareActionsDto.convert(gica));
        });

        core.model.ctc.GapsInCare gapInCareHasActions = gapsInCare.getGapInCare();
        gapInCareHasActions.setGapInCareStatusId(GapInCareStatuses.OPEN);

        GapInCareActionsDto gicadto = gapInCareActionsDto.convert(gapInCareHasActions, gapsInCare.getModifiedByName());
        gapInCareActions.add(gicadto);

      } else {
        GapInCareActionsDto gicadto = gapInCareActionsDto.convert(gapsInCare.getGapInCare(), gapsInCare.getModifiedByName());
        gapInCareActions.add(gicadto);

      }

      dto.setGapInCareActions(gapInCareActions);
    }

    return dto;
  }

  public Integer getGapInCareId() { return gapInCareId; }

  public void setGapInCareId(Integer gapInCareId) { this.gapInCareId = gapInCareId; }

  public String getGapName() {
    return gapName;
  }

  public void setGapName(String gapName) {
    this.gapName = gapName;
  }

  public List<GapInCareActionsDto> getGapInCareActions() {
    return gapInCareActions;
  }

  public void setGapInCareActions(List<GapInCareActionsDto> gapInCareActions) {
    this.gapInCareActions = gapInCareActions;
  }

  public String getGapStatus() {
    return gapStatus;
  }

  public void setGapStatus(String gapStatus) {
    this.gapStatus = gapStatus;
  }
}