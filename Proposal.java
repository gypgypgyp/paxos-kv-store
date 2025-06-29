public class Proposal {
  public final int proposalId;
  public final String key;
  public final String value;
  public final String operation;

  public Proposal(int proposalId, String key, String value, String operation) {
    this.proposalId = proposalId;
    this.key = key;
    this.value = value;
    this.operation = operation;
  }
}
