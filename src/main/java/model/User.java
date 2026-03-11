package model; 

//applicaiton user 

public class User { 

    private int userId;
    private String email;
    private String passwordHash;
    private String passwordSalt;

    public User() { 
    }
// new user 
    public User (int userId, String email, String passwordHash, String passwordSalt) { 
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }
//getters and setters 
    public int getUserId() { 
        return userId;
    }

    public void setUserId(int userId) { 
        this.userId = userId;
    }

    public String getEmail() { 
        return email;
    }

    public void setEmail(String email) { 

        this.email = email;
    }

    public String getPasswordHash() { 
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){ 
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() { 
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) { 
        this.passwordSalt = passwordSalt;
    }

@Override
public String toString() { 
    return "User{" + 
            "userId=" + userId +    
            ", email='" + email + '\'' + 
            '}';
}


}