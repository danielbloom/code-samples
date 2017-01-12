package api.modules.gapsincare.model;

/**
 * Created by bloomd on 10/27/16.
 */
public class GapInCareNamesInfo
{
  private Integer accountEntityId;

  private String firstName;

  private String lastName;


  public Integer getAccountEntityId() {
    return accountEntityId;
  }

  public void setAccountEntityId(Integer accountEntityId) {
    this.accountEntityId = accountEntityId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
