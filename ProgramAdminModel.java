public class ProgramAdminModel extends User{
	int nApproveAcct = 0;
	int nApproveRoom = 0;

	public ProgramAdminModel(String username, String password, int userType){
		super(username, password, 0);
	}
	
	public void addApproveAcct(){
		nApproveAcct++;
	}
	
	public void addApproveRoom(){
		nApproveRoom++;
	}
	
	public int getApproveAcct(){
		return nApproveAcct;
	}
	
	public int getApproveRoom(){
		return nApproveRoom;
	}
}