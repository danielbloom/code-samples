package api.modules.gapsincare.service

import api.TestBase
import api.modules.gapsincare.model.GapInCareActionInfo
import api.modules.gapsincare.model.GapInCareNamesInfo
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

import static org.mockito.Matchers.anyListOf

/**
 * Created by bloomd on 12/21/16.
 */
class GapsInCareServiceTest extends TestBase {

  @Mock
  private GapsInCareDAService _gapsInCareDAService;

  private GapsInCareService _gapsInCareServiceUnderTest;

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String GAP_NAME_DIABETES = "Diabetic retinal examination";
  private static final String GAP_NAME_HBA1C = "HbA1c test";
  private static final Integer GAP_IN_CARE_ID_1 = 1234;
  private static final Integer PATIENT_ID = 649;
  private static final String SAMPLE_FIRST_NAME_1 = "Stannis";
  private static final String SAMPLE_LAST_NAME_1 = "Baratheon";
  private static final String SAMPLE_FIRST_NAME_2 = "Sansa";
  private static final String SAMPLE_LAST_NAME_2 = "Stark";
  private static final Integer SAMPLE_ACCOUNT_ENTITY_ID_1 = 54321;
  private static final Integer SAMPLE_ACCOUNT_ENTITY_ID_2 = 12345;
  private static final String SAMPLE_OLD_DATE = "2016-11-17 17:51:38";
  private static final String SAMPLE_NEW_DATE = "2016-12-10 18:15:31";
  private static final String SAMPLE_USER_NAME_2 = "sample user 2";
  private static final String COMMENT = "Comment on an gap action";
  private static final String NOTES = "Notes on this gap";

  @Override
  public void before() {
    _gapsInCareServiceUnderTest = new GapsInCareService(_gapsInCareDAService);
  }

  @Test
  public void testProcessGapsInCare() {
    Mockito.when(_gapsInCareDAService.getNames(anyListOf(Integer))).thenReturn(getNames());

    Mockito.when(_gapsInCareDAService.getActions(anyListOf(Integer))).thenReturn(getActionsList());

    List<core.model.ctc.GapsInCare> gapsInCareList = getGapsInCare();
    List<GapsInCareListingInfo> gapsInCareListingInfoList = _gapsInCareServiceUnderTest.processGapsInCare(gapsInCareList , getAccountGapInCareTypes());

    Assert.assertEquals(getAccountGapInCareTypes().size(), gapsInCareListingInfoList.size());

    Assert.assertEquals(GAP_IN_CARE_ID_1, gapsInCareListingInfoList.get(0).getGapInCare().getId());
    Assert.assertEquals(GAP_NAME_DIABETES, gapsInCareListingInfoList.get(0).getGapInCare().getGapInCareTypeId().getName());
    Assert.assertEquals(SAMPLE_FIRST_NAME_1 + " " + SAMPLE_LAST_NAME_1, gapsInCareListingInfoList.get(0).getModifiedByName());
    GapInCareActionInfo gapInCareActionInfo = gapsInCareListingInfoList.get(0).getGapInCareActions().get(0);
    Assert.assertEquals(SAMPLE_FIRST_NAME_2 + " " + SAMPLE_LAST_NAME_2, gapInCareActionInfo.modifiedByName);
    Assert.assertEquals(COMMENT, gapInCareActionInfo.comment);

    Assert.assertNull(gapsInCareListingInfoList.get(1).getGapInCare());
    Assert.assertEquals(GAP_NAME_HBA1C, gapsInCareListingInfoList.get(1).getGapInCareType().getName());
    Assert.assertNull(gapsInCareListingInfoList.get(1).getModifiedByName());
    Assert.assertNull(gapsInCareListingInfoList.get(1).getGapInCareActions());
  }

  private List<GapsInCare> getGapsInCare() {

    core.model.ctc.GapsInCare gapsInCare = new core.model.ctc.GapsInCare();
    gapsInCare.setId(GAP_IN_CARE_ID_1);
    gapsInCare.setPatientId(PATIENT_ID);
    gapsInCare.setGapInCareTypeId(accountGapInCareTypes.get(0))
    gapsInCare.setDateIdentified(DATE_FORMAT.parse(SAMPLE_OLD_DATE));
    gapsInCare.setGapInCareStatusId(GapInCareStatuses.CLOSED);
    gapsInCare.setCreated(DATE_FORMAT.parse(SAMPLE_OLD_DATE));
    gapsInCare.setModified(DATE_FORMAT.parse(SAMPLE_NEW_DATE));
    gapsInCare.setCreatedBy(SAMPLE_ACCOUNT_ENTITY_ID_1);
    gapsInCare.setNotes(NOTES);

    List<core.model.ctc.GapsInCare> gapsInCareList = new ArrayList<core.model.ctc.GapsInCare>();

    gapsInCareList.add(gapsInCare);

    return gapsInCareList;
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

  private getNames() {
    List<GapInCareNamesInfo> gapInCareNamesInfoList = new ArrayList<GapInCareNamesInfo>();
    GapInCareNamesInfo gapInCareNamesInfo = new GapInCareNamesInfo();
    gapInCareNamesInfo.setAccountEntityId(SAMPLE_ACCOUNT_ENTITY_ID_1);
    gapInCareNamesInfo.setFirstName(SAMPLE_FIRST_NAME_1);
    gapInCareNamesInfo.setLastName(SAMPLE_LAST_NAME_1);
    gapInCareNamesInfoList.add(gapInCareNamesInfo);
    GapInCareNamesInfo gapInCareNamesInfo2 = new GapInCareNamesInfo();
    gapInCareNamesInfo2.setAccountEntityId(SAMPLE_ACCOUNT_ENTITY_ID_2);
    gapInCareNamesInfo2.setFirstName(SAMPLE_FIRST_NAME_2);
    gapInCareNamesInfo2.setLastName(SAMPLE_LAST_NAME_2);
    gapInCareNamesInfoList.add(gapInCareNamesInfo2);

    return gapInCareNamesInfoList;
  }

  private  List<GapInCareActionInfo> getActionsList() {
    List<GapInCareActionInfo> gapInCareActionsList = new ArrayList<GapInCareActionInfo>();
    GapInCareActionInfo gapInCareActions = new GapInCareActionInfo();
    gapInCareActions.setId(123);
    gapInCareActions.setGapInCareId(GAP_IN_CARE_ID_1)
    gapInCareActions.setCreated(DATE_FORMAT.parse(SAMPLE_NEW_DATE));
    gapInCareActions.setGapInCareStatusId(GapInCareStatuses.CLOSED)
    gapInCareActions.setCreatedBy(SAMPLE_ACCOUNT_ENTITY_ID_2);
    gapInCareActions.setComment(COMMENT);
    gapInCareActions.setModifiedByName(SAMPLE_USER_NAME_2)

    gapInCareActionsList.add(gapInCareActions);

    return gapInCareActionsList;
  }


}
