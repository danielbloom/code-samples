package api.modules.gapsincare.model;

import core.domain.value.GapInCareStatuses;

import java.util.Date;

/**
 * Created by bloomd on 10/27/16.
 */
public class GapInCareActionInfo
{
  private Integer id;

  private  Integer gapInCareId;

  private GapInCareStatuses gapInCareStatusId;

  private String comment;

  private Integer createdBy;

  private Integer gapInCareActionReasonId;

  private Date created;

  private String gapInCareActionReasonFriendlyId;

  private String gapInCareActionReasonName;

  private String modifiedByName;


  public String getModifiedByName() {
    return modifiedByName;
  }

  public void setModifiedByName(String modifiedByName) {
    this.modifiedByName = modifiedByName;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Integer getGapInCareId() {
    return gapInCareId;
  }

  public void setGapInCareId(Integer gapInCareId) {
    this.gapInCareId = gapInCareId;
  }

  public GapInCareStatuses getGapInCareStatusId() {
    return gapInCareStatusId;
  }

  public void setGapInCareStatusId(GapInCareStatuses gapInCareStatusId) {
    this.gapInCareStatusId = gapInCareStatusId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  public Integer getGapInCareActionReasonId() {
    return gapInCareActionReasonId;
  }

  public void setGapInCareActionReasonId(Integer gapInCareActionReasonId) {
    this.gapInCareActionReasonId = gapInCareActionReasonId;
  }

  public String getGapInCareActionReasonFriendlyId() {
    return gapInCareActionReasonFriendlyId;
  }

  public void setGapInCareActionReasonFriendlyId(String gapInCareActionReasonFriendlyId) {
    this.gapInCareActionReasonFriendlyId = gapInCareActionReasonFriendlyId;
  }

  public String getGapInCareActionReasonName() {
    return gapInCareActionReasonName;
  }

  public void setGapInCareActionReasonName(String gapInCareActionReasonName) {
    this.gapInCareActionReasonName = gapInCareActionReasonName;
  }
}
