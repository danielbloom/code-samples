package api.modules.gapsincare.dto;

import api.infrastructure.auditlog.AuditModelName;
import api.infrastructure.auditlog.DtoAuditLogging;
import api.modules.gapsincare.model.GapInCareActionInfo;
import api.modules.gapsincare.model.GapsInCareListingInfo;
import core.model.ctc.GapInCareActions;
import core.model.ctc.GapsInCare;

import java.text.SimpleDateFormat;

/**
 * Created by bloomd on 10/27/16.
 */
public class GapInCareActionsDto {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, YYYY");

  private String action;

  /**
  * This property is a concatenation of name and date date for the gap. This structure is used
  * in order to match the current FE setup. Will refactor in later release
  */
  private String by;

  private String result;

  private String notes;


  /**
   * Converts GapInCareAction objects into the actions listings for the FE
   *
   * @param gapInCareActions
   * @return GapInCareActionsDto
   */
  public GapInCareActionsDto convert(GapInCareActionInfo gapInCareActions) {
    GapInCareActionsDto gapInCareActionsDto = new GapInCareActionsDto();
    gapInCareActionsDto.setAction(gapInCareActions.getGapInCareStatusId().getFriendlyId());

    gapInCareActionsDto.setBy(gapInCareActions.getModifiedByName() + " on " + DATE_FORMAT.format(gapInCareActions.getCreated()));
    gapInCareActionsDto.setResult(gapInCareActions.getComment());

    return gapInCareActionsDto;
  }

  /**
   * Converts GapsInCare objects into actions listings for the FE
   *
   * @param gapsInCare
   * @param modifiedBy
   * @return GapInCareActionsDto
   */
  public GapInCareActionsDto convert(GapsInCare gapsInCare, String modifiedBy) {
    GapInCareActionsDto gapInCareActionsDto = new GapInCareActionsDto();
    gapInCareActionsDto.setNotes(gapsInCare.getNotes());
    gapInCareActionsDto.setBy(modifiedBy + " on " + DATE_FORMAT.format(gapsInCare.getCreated()));
    gapInCareActionsDto.setAction(gapsInCare.getGapInCareStatusId().getFriendlyId());

    return gapInCareActionsDto;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getBy() {
    return by;
  }

  public void setBy(String by) {
    this.by = by;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}