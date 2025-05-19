import physicianconnect.objects.User;

import java.util.*;

public class UserStub extends UnmodifiableStub{
    // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html
    private final AtomicInteger userIdCounter = new AtomicInteger(1);
    private final Map<Integer, User> users = new HashMap<>();

    private static final UserStub instance = new UserStub();

    private UserStub(){}

    public static UserStub getInstance(){
        return this.instance;
    }

    public User getUser(int userId){
        User toReturn = users.get(userId);
        if(toReturn == null) return null;

        return new User(
                toReturn.getUserId(),
                toReturn.getFirstName(),
                toReturn.getLastName(),
                toReturn.getEmail()
        );
    }

    public void addUser(User user){
        User toAdd = new User(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
        this.users.put(toAdd.getUserId(), toAdd);
    }

    public void updateUser(User toUpdate){
        if( users.containsKey(toUpdate.getUserId()) ) {
            User updatedUser = new User(
                toUpdate.getUserId(),
                toUpdate.getFirstName(),
                toUpdate.getLastName(),
                toUpdate.getEmail()
            );
            users.put(updatedUser.getUserId, updatedUser);
        }
    }

    public void deleteUser(int userId){
        users.remove(userId);
    }


}



