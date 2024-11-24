public class User{
	protected String username, idNum, password, questionAns;
	protected int idNum, userType, questionNum;
	 
	public User(String username, int idNum, String password, int userType){
		this.username = username;
		this.idNum = idNum;
		this.password = password; 
		this.userType = userType;
	}

	public void setQuestion(int questionNum, String questionAns){	 
		this.questionNum = questionNum;
		this.questionAns = questionAns;	
	}

	public int getidNum(){
		return idNum;
	}

	public String getUsername(){
		return username;		
	}
	 
	public String getPassword(){
		return password;
	}
	 
	 public String getQuestionAns(){
		return questionAns;
	}

	 public int getQuestionNum(){
		return questionNum;
	}

	public int getUserType(){
		return userType;
	}

}