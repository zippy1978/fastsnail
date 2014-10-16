package fr.grousset.fastsnail.demo.manager

import fr.grousset.fastsnail.async.AsyncManager
import fr.grousset.fastsnail.demo.model.github.Repo
import fr.grousset.fastsnail.transform.InjectComponent
import groovy.transform.CompileStatic
import retrofit.RestAdapter
import retrofit.http.GET
import retrofit.http.Path

public class RequestManager {

    @InjectComponent AsyncManager asyncManager

    private GitHubAPI gitHubAPI

    public RequestManager() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        gitHubAPI = restAdapter.create(GitHubAPI.class)
    }

    private interface GitHubAPI {

        @GET("/repos/{user}/{repo}")
        public Repo getRepo(@Path("user") String user, @Path("repo") String repo)
    }

    public rx.Observable getGitHubRepo(String user, String name) {

        return asyncManager.runAsync {
            gitHubAPI.getRepo(user, name)
        }
    }
}
