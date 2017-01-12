package api.modules.gapsincare.web;

import api.infrastructure.ICCMRestServerResource;
import api.infrastructure.server.resource.PatientResource;
import api.modules.gapsincare.dto.GapInCareActionsDto;
import api.modules.gapsincare.dto.GapsInCareDto;
import api.modules.gapsincare.model.GapsInCareListingInfo;
import api.modules.gapsincare.service.GapsInCareDAService;
import api.modules.gapsincare.service.GapsInCareService;
import api.modules.patientmedicationdispenses.dto.PatientDispenseDto;
import api.modules.security.authorization.RequiredAccessLevel;
import api.modules.security.authorization.ResourceAccessLevel;
import api.modules.security.enums.RbacModule;
import api.modules.shared.utils.InputOutputHelper;
import configuration.service.ICCMConfigurationService;
import core.domain.value.CodeSystems;
import core.domain.value.GapInCareStatuses;
import core.domain.value.MedicationActPharmacySupplyTypes;
import core.model.ctc.GapInCareActions;
import core.model.ctc.GapInCareTypes;
import core.service.persistence.IAccessValidationService;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.*;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bloomd on 8/5/16.
 */
public class GapsInCare extends PatientResource implements ICCMRestServerResource
{
  private final GapsInCareService gapsInCareService;
  private final GapsInCareDAService gapsInCareDAService;

  @Inject
  public GapsInCare(IAccessValidationService accessValidationService,
                    Logger logger,
                    ICCMConfigurationService configurationService,
                    InputOutputHelper inputOutputHelper,
                    GapsInCareDAService gapsInCareDAService,
                    GapsInCareService gapsInCareService) {
    super(accessValidationService, logger, configurationService, inputOutputHelper);
    this.gapsInCareDAService = gapsInCareDAService;
    this.gapsInCareService = gapsInCareService;
  }

  /**
   * GET API Endpoint for gaps in care data
   *
   * @return Json!
   */
  @Get("json")
  @RequiredAccessLevel(resourceAccessLevel = ResourceAccessLevel.READ, moduleName = RbacModule.GAPS_IN_CARE)
  public Representation getGapsInCare() {

    final List<core.model.ctc.GapsInCare> gapsInCareList = gapsInCareDAService.getGapsInCareList(getPatientId());

    final List<GapInCareTypes> accountGapInCareTypes = gapsInCareDAService.getAccountGapInCareTypes(configurationService.getAccountId());

    final List<GapsInCareListingInfo> gapsInCareListingInfo = gapsInCareService.processGapsInCare(gapsInCareList, accountGapInCareTypes);

    final List<GapsInCareDto> gapsInCareDtoList = gapsInCareListingInfo.stream().map(GapsInCareDto::convert).collect(Collectors.toList());

    return inputOutputHelper.setJsonResponseBody(gapsInCareDtoList);
  }

  /**
   * Parses all possible input from query params, and url params
   */
  public void doInit() {
    super.doInit();
  }

  @Override
  public List<String> getRelativeURL() {
    return Collections.singletonList("patients/{patientId}/gapsincare");
  }

  /**
   * {@inheritDoc}
   *
   * @param info
   */
  @Override
  public void describe(ApplicationInfo info) {
    DocumentationInfo docInfo = new DocumentationInfo("CCM web API documentation.");
    docInfo.setTitle("CCM Web API: gaps in care list for a patient.");
    info.getDocumentations().add(docInfo);
  }

  /**
   * {@inheritDoc}
   *
   * @param info
   */
  @Override
  protected void describeGet(MethodInfo info) {

    info.setDocumentation("GET a patient's gaps in care list and history.");

    info.setRequest(new RequestInfo());
    info.getRequest().getParameters().add(new ParameterInfo(PATIENT_ID_PARAMETER, true, "int", ParameterStyle.TEMPLATE, "Id of the patient"));

    RepresentationInfo repInfo = new RepresentationInfo(MediaType.APPLICATION_JSON);

    List<GapsInCareDto> gapsInCareDtoList = new ArrayList<GapsInCareDto>();
    GapsInCareDto gapsInCareDto = new GapsInCareDto();
    gapsInCareDto.setGapStatus(GapInCareStatuses.CLOSED.getName());
    gapsInCareDto.setGapName("Diabetic retinal examination");
    gapsInCareDto.setGapInCareId(123);

    List<GapInCareActionsDto> gapInCareActionsList = new ArrayList<GapInCareActionsDto>();

    GapInCareActionsDto gapInCareActionsDto2 = new GapInCareActionsDto();
    gapInCareActionsDto2.setAction(GapInCareStatuses.CLOSED.getName());
    gapInCareActionsDto2.setBy("Tywin Lannister on January 20, 2016");
    gapInCareActionsDto2.setResult("result of this action");
    gapInCareActionsDto2.setNotes(null);

    GapInCareActionsDto gapInCareActionsDto = new GapInCareActionsDto();
    gapInCareActionsDto.setAction(GapInCareStatuses.OPEN.getName());
    gapInCareActionsDto.setBy("Ned Stark on January 10, 2016");
    gapInCareActionsDto.setNotes("Some notes on this gap");
    gapInCareActionsDto.setResult(null);

    gapInCareActionsList.add(gapInCareActionsDto2);
    gapInCareActionsList.add(gapInCareActionsDto);

    gapsInCareDto.setGapInCareActions(gapInCareActionsList);

    gapsInCareDtoList.add(gapsInCareDto);

    GapsInCareDto gapsInCareDto2 = new GapsInCareDto();

    gapsInCareDto2.setGapStatus(GapInCareStatuses.NOTAPPLICABLE.getName());
    gapsInCareDto2.setGapName("HbA1c test");
    gapsInCareDtoList.add(gapsInCareDto2);

    String responseDocumentation = "Response: " + inputOutputHelper.getPrettyJson(Arrays.asList(gapsInCareDtoList));
    repInfo.setDocumentation(responseDocumentation);
    info.getResponse().getRepresentations().add(repInfo);
  }

}
