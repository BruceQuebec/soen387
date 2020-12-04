package soen387.chatroom.test;

import javafx.util.Pair;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import soen387.chatroom.model.*;
import soen387.chatroom.service.userservice.UserManagerImpl;
import soen387.chatroom.utils.Utils;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class MessageBoardImplTest {
   @Test
   public void parentGroupLabelTest() throws FileNotFoundException {
      // given
      final String group_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\group.json";

      // when
      List<Group> groups = Group.deserializationFromJson(group_file_path);
      List<Group> groups_with_parent = groups.stream().filter(group->!group.getParent().equals("")).collect(Collectors.toList());

      // then
      Assert.assertEquals(3, groups_with_parent.size());
   }

   @Test
   public void userMembershipLabelTest() throws FileNotFoundException {
      // given
      final String user_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\user.json";

      // when
      List<User> users = User.deserializationFromJson(user_file_path);
      Optional opt = users.stream().filter(user->user.getMembership().equals("")).findAny();

      // then
      Assert.assertFalse(opt.isPresent());
   }

   @Test
   public void adminGroupLabelTest() throws IOException {
      // given
      final String system_config_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\properties.properties";
      final String group_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\group.json";

      // when
      File file = new File(system_config_file_path);
      InputStream ips = new FileInputStream(file);
      Properties props = new Properties();
      props.load(ips);
      String admin_group_name = props.getProperty("admin_group_name");
      List<Group> groups = Group.deserializationFromJson(group_file_path);
      List<Group> group_admin = groups.stream().filter(group->group.getName().equals("admin")).collect(Collectors.toList());

      // then
      Assert.assertEquals(admin_group_name,group_admin.get(0).getName());
   }

   @Test
   public void userAuthenticationWrongUserPasswordTest() throws IllegalAccessException, InvocationTargetException, InstantiationException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, FileNotFoundException, NoSuchAlgorithmException {
      // given
      String className = "soen387.chatroom.service.userservice.UserManagerImpl";
      Map<String, String> testContext = new HashMap<>();
      testContext.put("password", "wrongpassword");
      testContext.put("username", "008z");
      testContext.put("userFilePath", "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\user.json");

      // when
      LocalUserManagerFactory localUserManagerFactory = new LocalUserManagerFactory(className);
      UserManagerImpl userManagerImpl = localUserManagerFactory.getUserManager();
      userManagerImpl.setContext(testContext);
      userManagerImpl.userAuthenticate();

      // then
      Assert.assertEquals(0, userManagerImpl.getUserMatched().size());
   }

   @Test
   public void userAuthenticationCorrectUserPasswordTest() throws IllegalAccessException, InvocationTargetException, InstantiationException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, FileNotFoundException, NoSuchAlgorithmException {
      // given
      String className = "soen387.chatroom.service.userservice.UserManagerImpl";
      Map<String, String> testContext = new HashMap<>();
      testContext.put("password", "40043261");
      testContext.put("username", "40043261");
      testContext.put("userFilePath", "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\user.json");

      // when
      LocalUserManagerFactory localUserManagerFactory = new LocalUserManagerFactory(className);
      UserManagerImpl userManagerImpl = localUserManagerFactory.getUserManager();
      userManagerImpl.setContext(testContext);
      userManagerImpl.userAuthenticate();

      // then
      Assert.assertEquals(1, userManagerImpl.getUserMatched().size());
      Assert.assertEquals("40043261", userManagerImpl.getUserMatched().get(0).getUsername());
      Assert.assertEquals("40043261@40043261.com", userManagerImpl.getUserMatched().get(0).getEmail());
      Assert.assertEquals("GinaCodyDept", userManagerImpl.getUserMatched().get(0).getMembership());
      Assert.assertEquals(3, userManagerImpl.getUserMatched().get(0).getUserId());
   }

   @Test
   public void adminUserGroupConstrainForPostCRUDTest() throws IOException, SQLException, ClassNotFoundException{
      // given
      final String group_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\group.json";
      Group userGroup = mock(Group.class);
      String userGroupName = "admin";
      String adminGroupName = "admin";
      String groupOfPost = "GinaCodyDept";
      List<Pair<Post, List<Pair<Integer, String>>>> outter_list = mock(ArrayList.class);
      Pair<Post, List<Pair<Integer, String>>> outter_pair = mock(Pair.class);
      Post post = Mockito.mock(Post.class);
      List<String> subGroups = new ArrayList<>();
      subGroups.add("admin");
      subGroups.add("public");
      subGroups.add("Concordia");
      subGroups.add("GinaCodyDept");
      subGroups.add("CompClass");
      subGroups.add("SoenClass");

      PowerMockito.mockStatic(Utils.class);
      Mockito.when(Utils.getAdminGroupNameKey(Mockito.anyString(), Mockito.anyObject())).thenReturn(adminGroupName);
      Mockito.when(outter_list.get(0)).thenReturn(outter_pair);
      Mockito.when(outter_pair.getKey()).thenReturn(post);
      Mockito.when(post.getGroupToSee()).thenReturn(groupOfPost);
      Mockito.when(userGroup.getSubGroups()).thenReturn(subGroups);

      // when
      boolean res = userGroupAuthCheck(userGroupName, outter_list, userGroup);

      // then
      Assert.assertTrue(res);
   }

   @Test
   public void rejectedNormalUserGroupConstrainForPostCRUDTest() throws IOException, SQLException, ClassNotFoundException{
      // given
      final String group_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\group.json";
      Group userGroup = mock(Group.class);
      String userGroupName = "CompClass";
      String adminGroupName = "admin";
      String groupOfPost = "GinaCodyDept";
      List<Pair<Post, List<Pair<Integer, String>>>> outter_list = mock(ArrayList.class);
      Pair<Post, List<Pair<Integer, String>>> outter_pair = mock(Pair.class);
      Post post = Mockito.mock(Post.class);
      List<String> subGroups = new ArrayList<>();

      PowerMockito.mockStatic(Utils.class);
      Mockito.when(Utils.getAdminGroupNameKey(Mockito.anyString(), Mockito.anyObject())).thenReturn(adminGroupName);
      Mockito.when(outter_list.get(0)).thenReturn(outter_pair);
      Mockito.when(outter_pair.getKey()).thenReturn(post);
      Mockito.when(post.getGroupToSee()).thenReturn(groupOfPost);
      Mockito.when(userGroup.getSubGroups()).thenReturn(subGroups);

      // when
      boolean res = userGroupAuthCheck(userGroupName, outter_list, userGroup);

      // then
      Assert.assertFalse(res);
   }

   @Test
   public void approvedNormalUserGroupConstrainForPostCRUDTest() throws IOException, SQLException, ClassNotFoundException{
      // given
      final String group_file_path = "D:\\study\\concordia\\2020\\fall\\SOEN387\\project\\chatroom\\src\\main\\webapp\\WEB-INF\\classes\\group.json";
      Group userGroup = mock(Group.class);
      String userGroupName = "GinaCodyDept";
      String adminGroupName = "admin";
      String groupOfPost = "SoenClass";
      List<Pair<Post, List<Pair<Integer, String>>>> outter_list = mock(ArrayList.class);
      Pair<Post, List<Pair<Integer, String>>> outter_pair = mock(Pair.class);
      Post post = Mockito.mock(Post.class);
      List<String> subGroups = new ArrayList<>();
      subGroups.add("CompClass");
      subGroups.add("SoenClass");

      PowerMockito.mockStatic(Utils.class);
      Mockito.when(Utils.getAdminGroupNameKey(Mockito.anyString(), Mockito.anyObject())).thenReturn(adminGroupName);
      Mockito.when(outter_list.get(0)).thenReturn(outter_pair);
      Mockito.when(outter_pair.getKey()).thenReturn(post);
      Mockito.when(post.getGroupToSee()).thenReturn(groupOfPost);
      Mockito.when(userGroup.getSubGroups()).thenReturn(subGroups);

      // when
      boolean res = userGroupAuthCheck(userGroupName, outter_list, userGroup);

      // then
      Assert.assertTrue(res);
   }

   private boolean userGroupAuthCheck(String userGroupName, List<Pair<Post, List<Pair<Integer, String>>>> post, Group userGroupObj) throws IOException {
      if( userGroupName.equals(Utils.getAdminGroupNameKey("", null)) ||
              post.get(0).getKey().getGroupToSee().equals("Public") ||
              userGroupName.equals(post.get(0).getKey().getGroupToSee()) ||
              userGroupObj.getSubGroups().stream().filter(subGroup->subGroup.equals(post.get(0).getKey().getGroupToSee())).findAny().isPresent()){
         return true;
      }
      return false;
   }
}
