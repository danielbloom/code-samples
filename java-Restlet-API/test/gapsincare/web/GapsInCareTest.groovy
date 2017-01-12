package api.modules.gapsincare.web

import api.infrastructure.server.resource.CCMResource
import api.infrastructure.server.resource.PatientResourceTestBase
import api.modules.gapsincare.dto.GapInCareActionsDto
import api.modules.gapsincare.dto.GapsInCareDto
import api.modules.gapsincare.model.GapInCareActionInfo
import api.modules.gapsincare.model.GapsInCareListingInfo
import api.modules.gapsincare.service.GapsInCareDAService
import api.modules.gapsincare.service.GapsInCareService
import api.modules.shared.utils.InputOutputHelper
import configuration.service.ICCMConfigurationService
import core.domain.value.GapInCareStatuses
import core.model.ctc.CarePlanGoals
import core.model.ctc.GapInCareTypes
import core.service.persistence.IAccessValidationService
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import org.restlet.Request
import org.restlet.representation.Representation
import org.restlet.representation.StringRepresentation
import org.restlet.resource.Resource
import org.slf4j.Logger

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentMap

import static org.mockito.Matchers.anyInt
import static org.mockito.Matchers.anyListOf

/**
 * Created by bloomd on 12/21/16.
 */
@RunWith(MockitoJUnitRunner.class)
class GapsInCareTest extends PatientResourceTestBase {

  @Mock
  private IAccessValidationService _accessValidationService;
  @Mock
  private Logger _logger;
  @Mock
  private ICCMConfigurationService _configurationService;
  @Mock
  private InputOutputHelper inputOutputHelper;
  @Mock
  private GapsInCareDAService _gapsInCareDAService;
  @Mock
  private GapsInCareService _gapsInCareService;
  @Mock
  private Request _request;
  @Mock
  private ConcurrentMap<String, Object> _requestAttributes;

  private static final Integer GAP_IN_CARE_ID_1 = 1234;
  private static final String MODIFIED_BY_NAME = "Sample Account Admin";
  private static final String UUID = "f166efc8-1550-4143-a579-7c1e686dbe41"
  private static final String SAMPLE_OLD_DATE = "2016-11-17 17:51:38";
  private static final String SAMPLE_NEW_DATE = "2016-12-10 18:15:31";
  private static final String SAMPLE_USER_NAME_2 = "sample user 2";
  private static final String GAP_NAME_DIABETES = "Diabetic retinal examination";

  private static final String COMMENT = "Comment on an gap action";
  private GapsInCare _gapsInCareUnderTest;

  @Override
  protected CCMResource setTestClass() {
    inputOutputHelper = new InputOutputHelper();
    return  new GapsInCare(_accessValidationService, _logger, _configurationService, inputOutputHelper, _gapsInCareDAService, _gapsInCareService);
  }

  @Override
  public void before() {

    Mockito.when(_request.getAttributes()).thenReturn(_requestAttributes);
    Mockito.when(_request.getAttributes().get("patientId")).thenReturn(PATIENT_ID);

    _gapsInCareUnderTest = setTestClass();
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
    gapsInCare.setNotes("notes on this gap");

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

    List<GapsInCareListingInfo> gapsInCareListingInfoList = new ArrayList<GapsInCareListingInfo>();
    GapsInCareListingInfo gapsInCareListingInfo1 = new GapsInCareListingInfo();
    gapsInCareListingInfo1.setGapInCare(gapsInCare);
    gapsInCareListingInfo1.setGapInCareType(accountGapInCareTypes.get(0));
    gapsInCareListingInfo1.setGapInCareActions(gapInCareActionsList);
    gapsInCareListingInfo1.setModifiedByName(MODIFIED_BY_NAME);

    gapsInCareListingInfoList.add(gapsInCareListingInfo1);

    Mockito.when(_gapsInCareDAService.getGapsInCareList(anyInt())).thenReturn(gapsInCareList);

    Mockito.when(_gapsInCareDAService.getAccountGapInCareTypes(anyInt())).thenReturn(accountGapInCareTypes);

    Mockito.when(_gapsInCareService.processGapsInCare(anyListOf(core.model.ctc.GapsInCare), anyListOf(GapInCareTypes))).thenReturn(gapsInCareListingInfoList);
  }

  @Test
  public void testGetGapsInCare() {

    Representation result = _gapsInCareUnderTest.getGapsInCare();
    List<GapsInCareDto> gapsInCare = getGapsInCareFromResponse(result);

    Assert.assertEquals(GAP_IN_CARE_ID_1, ((Integer) gapsInCare.get(0).gapInCareId));
    Assert.assertEquals(2, gapsInCare.get(0).gapInCareActions.size());

    List<GapInCareActionsDto> gapInCareActionsDtos = gapsInCare.get(0).getGapInCareActions();

    // Is the oldest action an open
    Assert.assertEquals(GapInCareStatuses.OPEN.friendlyId, gapInCareActionsDtos.get(gapInCareActionsDtos.size() -1).action);
    Assert.assertEquals(GAP_NAME_DIABETES, gapsInCare.get(0).gapName);
    Assert.assertEquals(GapInCareStatuses.CLOSED.friendlyId, gapsInCare.get(0).gapStatus)
  }

  @Test
  public void testGetRelativeUrl() {
    List<String> url = _gapsInCareUnderTest.getRelativeURL();
    Assert.assertEquals("patients/{patientId}/gapsincare", url.get(0));
  }

  private List<GapInCareTypes> getAccountGapInCareTypes()
  {
    GapInCareTypes gapInCareTypes1 = new GapInCareTypes();
    gapInCareTypes1.setId(1);
    gapInCareTypes1.setName(GAP_NAME_DIABETES);
    GapInCareTypes gapInCareTypes2 = new GapInCareTypes();
    gapInCareTypes2.setId(2);
    gapInCareTypes2.setName("HbA1c test");
    GapInCareTypes gapInCareTypes3 = new GapInCareTypes();
    gapInCareTypes3.setId(3);
    gapInCareTypes3.setName("Microalbuminuria test");

    List<GapInCareTypes> accountGapInCareTypes = new ArrayList<GapInCareTypes>();
    accountGapInCareTypes.add(gapInCareTypes1);
    accountGapInCareTypes.add(gapInCareTypes2);
    accountGapInCareTypes.add(gapInCareTypes3);

    return accountGapInCareTypes;
  }

  /**
   * Takes in a response object and converts the json into
   * GapsInCareDto objects.
   *
   * @param response A request object with a list of GapsInCareDtop nodes
   * @return List<GapsInCareDto> a List of GapsInCareDto objects cointained within the response
   */
  private List<GapsInCareDto> getGapsInCareFromResponse(StringRepresentation response)
  {
    String resultJson = response.getText();
    Gson gson = new Gson();
    List<GapsInCareDto> gapsInCareDtos = new ArrayList<GapsInCareDto>();

    gapsInCareDtos = (List<CarePlanGoals>) gson.fromJson(resultJson, (Class<?>) gapsInCareDtos.getClass());

    return gapsInCareDtos;
  }

}
