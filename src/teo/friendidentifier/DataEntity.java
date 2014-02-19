package teo.friendidentifier;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DataEntity {

	@Id
	private String id;
	private String owner;
	private String friend;
	
	public DataEntity(){		
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getFriend() {
		return friend;
	}
	public void setFriend(String friend) {
		this.friend = friend;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


}
