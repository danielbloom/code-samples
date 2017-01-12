package api.modules.gapsincare.dto

import api.TestBase
import api.modules.gapsincare.model.GapInCareActionInfo
import core.domain.value.GapInCareStatuses
import core.model.ctc.GapsInCare
import org.junit.Assert
import org.junit.Test

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by bloomd on 12/21/16.
 */
class GapInCareActionsDtoTest extends TestBase {
  private static final String NOTES = "notes: a different time, a different king";
  private static final String COMMENT = "comments: you win or you die";
  private static final String MODIFIED_BY = "Roose Bolton";
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String SAMPLE_OLD_DATE = "2016-11-17 17:51:38";

  private GapInCareActionsDto _gapInCareActionsDtoUnderTest;

  @Override
  public void before() {
    _gapInCareActionsDtoUnderTest = new GapInCareActionsDto();
  }

  @Test
  public void testConvertAction() {

    GapInCareActionInfo gapInCareActionInfo = new GapInCareActionInfo();
    gapInCareActionInfo.setComment(COMMENT)
    gapInCareActionInfo.setCreated(DATE_FORMAT.parse(SAMPLE_OLD_DATE));
    gapInCareActionInfo.setModifiedByName(MODIFIED_BY);
    gapInCareActionInfo.setGapInCareStatusId(GapInCareStatuses.CLOSED);
    GapInCareActionsDto result = _gapInCareActionsDtoUnderTest.convert(gapInCareActionInfo);

    Assert.assertEquals(MODIFIED_BY + " on " + DATE_FORMAT.parse(SAMPLE_OLD_DATE).format("MMMM d, YYYY"), result.getBy());
    Assert.assertEquals(GapInCareStatuses.CLOSED.getFriendlyId(), result.action);
    Assert.assertEquals(COMMENT, result.result);
  }

  @Test
  public void testConvertGapInCare() {

    GapsInCare gapInCare = new GapsInCare();
    gapInCare.setNotes(NOTES);
    gapInCare.setCreated(DATE_FORMAT.parse(SAMPLE_OLD_DATE));
    gapInCare.setGapInCareStatusId(GapInCareStatuses.OPEN);
    GapInCareActionsDto result = _gapInCareActionsDtoUnderTest.convert(gapInCare, MODIFIED_BY);

    Assert.assertEquals(MODIFIED_BY + " on " + DATE_FORMAT.parse(SAMPLE_OLD_DATE).format("MMMM d, YYYY"), result.getBy());
    Assert.assertEquals(GapInCareStatuses.OPEN.getFriendlyId(), result.action);
    Assert.assertEquals(NOTES, result.notes);
  }
}
