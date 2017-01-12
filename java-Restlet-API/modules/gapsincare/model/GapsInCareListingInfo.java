package api.modules.gapsincare.model;

import core.model.ctc.GapInCareTypes;
import core.model.ctc.GapsInCare;

import java.util.List;

/**
 * Created by bloomd on 10/27/16.
 */
public class GapsInCareListingInfo {

  private GapsInCare gapInCare;
  private GapInCareTypes gapInCareType;
  private List<GapInCareActionInfo> gapInCareActions;
  private String modifiedByName;

  public GapInCareTypes getGapInCareType() {
    return gapInCareType;
  }

  public void setGapInCareType(GapInCareTypes gapInCareType) {
    this.gapInCareType = gapInCareType;
  }

  public String getModifiedByName() {
    return modifiedByName;
  }

  public void setModifiedByName(String modifiedByName) {
    this.modifiedByName = modifiedByName;
  }

  public GapsInCare getGapInCare() {
    return gapInCare;
  }

  public void setGapInCare(GapsInCare gapInCare) {
    this.gapInCare = gapInCare;
  }

  public List<GapInCareActionInfo> getGapInCareActions() {
    return gapInCareActions;
  }

  public void setGapInCareActions(List<GapInCareActionInfo> gapInCareActions) {
    this.gapInCareActions = gapInCareActions;
  }
}
