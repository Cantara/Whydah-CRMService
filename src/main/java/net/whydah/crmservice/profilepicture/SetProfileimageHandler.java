package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.profilepicture.model.ProfilePicture;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MediaType;
import ratpack.http.Request;
import ratpack.http.TypedData;

import java.sql.SQLIntegrityConstraintViolationException;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class SetProfileimageHandler implements Handler {

    private final ProfilepictureRepository repository;

    @Inject
    public SetProfileimageHandler(ProfilepictureRepository repository) {
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
                repository.updateProfileimage(customerRef, new ProfilePicture(data.getBytes(), contentType.getType()));
            }).onError(throwable -> {
                if (throwable instanceof SQLIntegrityConstraintViolationException) {
                    ctx.clientError(400); //Bad request
                }
            }).then(() -> {
                ctx.redirect(201, customerRef); //Created
            });
        });
    }
}
