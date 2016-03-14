package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.profilepicture.model.ProfileImage;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MediaType;
import ratpack.http.Request;
import ratpack.http.TypedData;

import java.sql.SQLIntegrityConstraintViolationException;

@Singleton
public class CreateProfileImageHandler implements Handler {

    private final ProfileImageRepository repository;

    @Inject
    public CreateProfileImageHandler(ProfileImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        Request request = ctx.getRequest();

        MediaType contentType = request.getContentType();
        Promise<TypedData> bodyPromise = request.getBody();

        bodyPromise.then(data -> {
            Blocking.op(() -> {
                repository.createProfileImage(customerRef, new ProfileImage(data.getBytes(), contentType.getType()));
            }).onError(throwable -> {
                if (throwable instanceof SQLIntegrityConstraintViolationException) {
                    ctx.clientError(400); //Bad request
                }
            }).then(() -> {
                ctx.redirect(201,  "image"); //Created
            });
        });
    }
}
