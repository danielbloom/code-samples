package api.modules.gapsincare.dto

import api.TestBase
import api.modules.gapsincare.model.GapInCareActionInfo
import api.modules.gapsincare.model.GapsInCareListingInfo
import core.domain.value.GapInCareStatuses
import core.model.ctc.GapInCareTypes
import core.model.ctc.GapsInCare
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

import java.text.DateFormat
import java.text.SimpleDateFormat

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyListOf
import static org.mockito.Matchers.anyString


/**
 * Created by bloomd on 12/21/16.
 */
class GapsInCareDtoTest extends TestBase {
  @Mock
  GapInCareActionsDto _gapInCareActionsDto;

  private static final Integer GAP_IN_CARE_ID_1 = 1234;
  private static final Integer PATIENT_ID = 649;
  private static final String MODIFIED_BY_NAME = "Sample Account Admin";
  private static final String UUID = "f166efc8-1550-4143-a579-7c1e686dbe41"
  private static final String SAMPLE_OLD_DATE = "2016-11-17 17:51:38";
  private static final String SAMPLE_NEW_DATE = "2016-12-10 18:15:31";
  private static final String SAMPLE_USER_NAME_2 = "sample user 2";
  private static final String GAP_NAME_DIABETES = "Diabetic retinal examination";
  private static final String GAP_NAME_HBA1C = "HbA1c test";
  private static final String SAMPLE_AUTHOR_TEXT = "Theon Greyjoy on Sept 27, 2016";
  private static final String COMMENT = "As a result I'm making this comment";
  private static final String NOTES = "notes: a different time, a different king";

  private GapsInCareDto _gapsInCareDtoUnderTest;


  @Override
  public void before() {
    _gapsInCareDtoUnderTest = new GapsInCareDto();

  }

  @Test
  public void testGapsInCareDto() {
    GapInCareActionsDto gapActionDtoFromAction = new GapInCareActionsDto();
    gapActionDtoFromAction.setAction(GapInCareStatuses.CLOSED.friendlyId);
    gapActionDtoFromAction.setBy(SAMPLE_AUTHOR_TEXT)
    gapActionDtoFromAction.setResult(COMMENT);
    GapInCareActionsDto gapActionDtoFromGap = new GapInCareActionsDto();
    gapActionDtoFromGap.setAction(GapInCareStatuses.CLOSED.friendlyId);
    gapActionDtoFromGap.setBy(SAMPLE_AUTHOR_TEXT)
    gapActionDtoFromGap.setResult(COMMENT);

    Mockito.when(_gapInCareActionsDto.convert(any(GapInCareActionInfo))).thenReturn(gapActionDtoFromAction);
    Mockito.when(_gapInCareActionsDto.convert(any(GapsInCare), anyString())).thenReturn(gapActionDtoFromGap);

    GapsInCareListingInfo gapsInCare = getGapsInCare();
    GapsInCareDto result = _gapsInCareDtoUnderTest.convert(gapsInCare);

    Assert.assertEquals(GAP_NAME_DIABETES, result.gapName);
    Assert.assertEquals(GapInCareStatuses.CLOSED.getFriendlyId(), result.gapStatus);
    Assert.assertEquals(GAP_IN_CARE_ID_1, result.gapInCareId);

    Assert.assertEquals(GapInCareStatuses.CLOSED.friendlyId, result.gapInCareActions.get(0).action);
    Assert.assertEquals(COMMENT, result.gapInCareActions.get(0).result);
    Assert.assertEquals(NOTES, result.gapInCareActions.get(1).notes);
    Assert.assertEquals(GapInCareStatuses.OPEN.friendlyId, result.gapInCareActions.get(result.gapInCareActions.size() -1).action);
  }


  private GapsInCareListingInfo getGapsInCare() {
    List<GapInCareTypes> accountGapInCareTypes = getAccountGapInCareTypes();

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    core.model.ctc.GapsInCare gapsInCare = new core.model.ctc.GapsInCare();
    gapsInCare.setId(GAP_IN_CARE_ID_1);
    gapsInCare.setPatientId(PATIENT_ID);
    gapsInCare.setGapInCareTypeId(accountGapInCareTypes.get(0))
    gapsInCare.setDateIdentified(df.parse(SAMPLE_OLD_DATE));
    gapsInCare.setGapInCareStatusId(GapInCareStatuses.CLOSED);
    gapsInCare.setCreated(df.parse(SAMPLE_OLD_DATE));
    gapsInCare.setModified(df.parse(SAMPLE_NEW_DATE));
    gapsInCare.setCreatedBy(300);
    gapsInCare.setNotes(NOTES);

    List<core.model.ctc.GapsInCare> gapsInCareList = new ArrayList<core.model.ctc.GapsInCare>();

    gapsInCareList.add(gapsInCare);

    List<GapInCareActionInfo> gapInCareActionsList = new ArrayList<GapInCareActionInfo>();
    GapInCareActionInfo gapInCareActions = new GapInCareActionInfo();
    gapInCareActions.setId(123);
    gapInCareActions.setGapInCareId(GAP_IN_CARE_ID_1)
    gapInCareActions.setCreated(df.parse(SAMPLE_NEW_DATE));
    gapInCareActions.setGapInCareStatusId(GapInCareStatuses.CLOSED)
    gapInCareActions.setCreatedBy(300);
    gapInCareActions.setComment(COMMENT);
    gapInCareActions.setModifiedByName(SAMPLE_USER_NAME_2)

    gapInCareActionsList.add(gapInCareActions);

    GapsInCareListingInfo gapsInCareListingInfo1 = new GapsInCareListingInfo();
    gapsInCareListingInfo1.setGapInCare(gapsInCare);
    gapsInCareListingInfo1.setGapInCareType(accountGapInCareTypes.get(0));
    gapsInCareListingInfo1.setGapInCareActions(gapInCareActionsList);
    gapsInCareListingInfo1.setModifiedByName(MODIFIED_BY_NAME);

    return gapsInCareListingInfo1;
  }

  private List<GapInCareTypes> getAccountGapInCareTypes() {
    GapInCareTypes gapInCareTypes1 = new GapInCareTypes();
    gapInCareTypes1.setId(1);
    gapInCareTypes1.setName(GAP_NAME_DIABETES);
    GapInCareTypes gapInCareTypes2 = new GapInCareTypes();
    gapInCareTypes2.setId(2);
    gapInCareTypes2.setName(GAP_NAME_HBA1C);
    GapInCareTypes gapInCareTypes3 = new GapInCareTypes();
    gapInCareTypes3.setId(3);
    gapInCareTypes3.setName("Microalbuminuria test");

    List<GapInCareTypes> accountGapInCareTypes = new ArrayList<GapInCareTypes>();
    accountGapInCareTypes.add(gapInCareTypes1);
    accountGapInCareTypes.add(gapInCareTypes2);
    accountGapInCareTypes.add(gapInCareTypes3);

    return accountGapInCareTypes;
  }
}
