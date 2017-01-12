package api.modules.gapsincare.service;

import api.modules.gapsincare.model.GapInCareActionInfo;
import api.modules.gapsincare.model.GapInCareNamesInfo;
import core.model.ctc.*;
import com.advisory.pt.xede.util.querydsl.ISQLQueryBuilder;
import com.mysema.query.QueryFlag;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Projections;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bloomd on 11/11/16.
 */
public class GapsInCareDAService {
  private final ISQLQueryBuilder sqlQueryBuilder;

  @Inject
  public GapsInCareDAService(ISQLQueryBuilder sqlQueryBuilder) {
    this.sqlQueryBuilder = sqlQueryBuilder;
  }

  /**
   * Get all gaps in care for this patient
   *
   * @param patientId
   * @return List<GapsInCare>
   */
  public List<GapsInCare> getGapsInCareList(final int patientId) {
    QGapsInCare qGapsInCare = new QGapsInCare("gapsInCare");

    final SQLQuery query = sqlQueryBuilder.getSQLQuery();
    query.from(qGapsInCare)
        .where(qGapsInCare.patientId.eq(patientId))
        .groupBy(qGapsInCare.gapInCareStatusId, qGapsInCare.id)
        .orderBy(qGapsInCare.created.desc());

    return query.list(qGapsInCare);
  }


  /**
   * Get all gap in care types that are used by this account
   *
   * @param accountId
   * @return List<GapInCareTypes>
   */
  public List<GapInCareTypes> getAccountGapInCareTypes(final int accountId) {
    QGapInCareTypes qGapInCareTypes = new QGapInCareTypes("gapsInCareTypes");
    QAccountGapInCareTypes qAccountGapInCareTypes = new QAccountGapInCareTypes("accountGapInCareTypes");

    final SQLQuery query = sqlQueryBuilder.getSQLQuery();
    query.from(qGapInCareTypes)
        .join(qAccountGapInCareTypes).on(qAccountGapInCareTypes.gapInCareTypeId.eq(qGapInCareTypes.id))
        .where(qAccountGapInCareTypes.accountId.eq(accountId)
            .and(qGapInCareTypes.deleted.isFalse())
            .and(qAccountGapInCareTypes.deleted.isFalse()));

    return query.list(qGapInCareTypes);
  }

  /**
   * Get all actions associated with this patient's gaps
   *
   * @param gapInCareIds
   * @return List<GapInCareActionInfo>
   */
  public List<GapInCareActionInfo> getActions(List<Integer> gapInCareIds) {

    QGapInCareActions qGapInCareActions = new QGapInCareActions("gapInCareActions");
    QGapInCareActionReasons qGapInCareActionReasons = new QGapInCareActionReasons("gapInCareActionReasons");

    final SQLQuery query = sqlQueryBuilder.getSQLQuery();
    query.from(qGapInCareActions)
        .join(qGapInCareActionReasons).on(qGapInCareActionReasons.id.eq(qGapInCareActions.gapInCareActionReasonId))
        .where(qGapInCareActions.gapInCareId.in(gapInCareIds))
        .orderBy(qGapInCareActions.created.desc());

    return query.list(Projections.bean(GapInCareActionInfo.class,
        qGapInCareActions.id,
        qGapInCareActions.gapInCareId,
        qGapInCareActions.gapInCareStatusId,
        qGapInCareActions.comment,
        qGapInCareActions.createdBy,
        qGapInCareActions.created,
        qGapInCareActions.gapInCareActionReasonId,
        qGapInCareActionReasons.friendlyId,
        qGapInCareActionReasons.name)
    );

  }

  /**
   * Lookup any names associated with gaps in care or actions for this patient
   *
   * @param accountEntityIdsForNameLookup
   * @return List<GapInCareNamesInfo>
   */
  public List<GapInCareNamesInfo> getNames(List<Integer> accountEntityIdsForNameLookup) {
    List<Integer> aeIds = accountEntityIdsForNameLookup.stream().distinct().collect(Collectors.toList());

    QPeople qPeople = new QPeople("people");
    QAccountEntities qAccountEntities = new QAccountEntities("accountEntities");

    final SQLQuery query = sqlQueryBuilder.getSQLQuery();
    query.from(qPeople)
        .join(qAccountEntities).on(qAccountEntities.entityId.eq(qPeople.id))
        .where(qAccountEntities.id.in(aeIds));

    return query.list(Projections.bean(GapInCareNamesInfo.class,
        qAccountEntities.id.as("accountEntityId"),
        qPeople.firstName,
        qPeople.lastName)
    );
  }

}
