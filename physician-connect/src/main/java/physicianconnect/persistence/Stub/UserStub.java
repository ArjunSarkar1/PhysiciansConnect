import physicianconnect.objects.User;

import java.util.*;

public class UserStub{
//    https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html
//    potentially look into these for the upcomking interations -Pankaj, May 19


    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    private static final UserStub instance = new UserStub();

    private UserStub(){}

    public static UserStub getInstance(){
        return this.instance;
    }

    // input is done assuming it is a valid userId i.e within valid range as such,
    // otherwise the rest of the checks will be handled by the logic layer
    public User getUser(int userId){

        User toReturn = users.get(userId);
        User result = null;
        if(toReturn != null){
            result = new User(
                toReturn.getUserId(),
                toReturn.getFirstName(),
                toReturn.getLastName(),
                toReturn.getEmail()
            );
        }
        // from here have the logic layer handle null error, if those are present
        return result;
    }

    public Map<Integer, User> getAllUsers() {
        return Collections.unmodifiableMap(users);
    }

    // input is assuming that we have a valid user, this should be getting validated in the logic layer
    // and that the user does not have an userId already attached to it
    public User addUser(User user){

        User toAdd = new User(
                ++this.userId,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
        this.users.put(toAdd.getUserId(), toAdd);
    }

//    Input: valid userId, logic handles validation
//    Output: if DNE then null, logic handles rest, it does then update and return the updated User.
    public User updateUser(User toUpdate){
        User updatedUser = null;
        if( users.containsKey(toUpdate.getUserId()) ) {
            updatedUser = new User(
                toUpdate.getUserId(),
                toUpdate.getFirstName(),
                toUpdate.getLastName(),
                toUpdate.getEmail()
            );
            users.put(updatedUser.getUserId, updatedUser);
        }
        return updatedUser;
    }

//    Input: valid userId, logic handles validation
//    Output: if DNE then null, logic handles rest, it does then make a copy to return, delete from DB and return copy
    public User deleteUser(int userId){
        User toDelete = users.get(userId);
        User result = null;
        if(toReturn != null){
            result = new User(
                    toDelete.getUserId(),
                    toDelete.getFirstName(),
                    toDelete.getLastName(),
                    toDelete.getEmail()
            );
            this.users.remove(userId);
        }
        return result;
    }


}



