
public class User{
 protected String username, password, questionAns;
 protected int userType, questionNum;
 
 public User(String username, String password, int userType){
	 this.username = username;
	 this.password = password; 
	 this.userType = userType;
	 
 }

public void setQuestion(int questionNum, String questionAns){
	 
	 this.questionNum = questionNum;
	 this.questionAns = questionAns;
	
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