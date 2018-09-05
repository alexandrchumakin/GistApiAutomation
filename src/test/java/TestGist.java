import common.Configurations;
import common.TokenString;
import http.GistRequest;
import model.Gist;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGist {
    private static TokenString token;
    private static GistRequest req;
    private static List<String> createdGists = new ArrayList<>();

    @BeforeClass
    public static void setUp(){
        req = new GistRequest();
        token = req.auth();
    }

    @AfterClass
    public static void tearDown(){
        for (String gistId : createdGists){
            req.deleteGist(gistId);
        }
        
        req.removeToken(token);
    }
    
    private Gist newGist(){
        Gist gist = req.createGist();
        createdGists.add(gist.getId());
        return gist;
    }

    @Test
    public void testUserEmail(){
        String userEmail = req.getUser().getEmail();
        Assert.assertEquals(Configurations.getValueByKey("userEmail"), userEmail);
    }

    @Test
    public void createGist(){
        String fileName = TokenString.generateString();
        Gist gist = req.createGist(fileName);
        Assert.assertEquals(fileName, gist.getFileName());
        createdGists.add(gist.getId());
    }

    @Test
    public void testGetGist(){
        Gist gist = newGist();
        String gistId = req.getGist(gist.getId()).getId();
        Assert.assertEquals(gist.getId(), gistId);
        createdGists.add(gist.getId());
    }

    @Test
    public void testGetGists(){
        Gist gist = newGist();
        List<String> allGists = req.getAllGistIds();
        Assert.assertTrue(allGists.contains(gist.getId()));
    }

    @Test
    public void testUsersGists(){
        Gist gist = newGist();
        List<String> allGists = req.getUsersGistIds();
        Assert.assertTrue(allGists.contains(gist.getId()));
    }

    @Test
    public void testPublicGists(){
        Gist gist = newGist();
        List<String> allGists = req.getPublicGistIds();
        Assert.assertTrue(allGists.contains(gist.getId()));
    }

    @Test
    public void testStarGist(){
        Gist gist = newGist();
        int status = req.starGist(gist.getId());
        Assert.assertEquals(204, status);
    }

    @Test
    public void testStarGistAgain(){
        Gist gist = newGist();
        req.starGist(gist.getId());
        int status = req.starGist(gist.getId());
        Assert.assertEquals(204, status);
    }

    @Test
    public void testUnstarGist(){
        Gist gist = newGist();
        req.starGist(gist.getId());
        int status = req.unstarGist(gist.getId());
        Assert.assertEquals(204, status);
    }

    @Test
    public void testUnstarGistWithoutStar(){
        Gist gist = newGist();
        int status = req.unstarGist(gist.getId());
        Assert.assertEquals(204, status);
    }

    @Test
    public void testGistIsStarred(){
        Gist gist = newGist();
        req.starGist(gist.getId());
        int status = req.checkGistStarred(gist.getId());
        Assert.assertEquals(204, status);
    }

    @Test
    public void testGistIsNotStarred(){
        Gist gist = newGist();
        int status = req.checkGistStarred(gist.getId());
        Assert.assertEquals(404, status);
    }
}
