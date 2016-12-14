/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-10-17 16:43:55 UTC)
 * on 2016-12-05 at 10:04:32 UTC 
 * Modify at your own risk.
 */

package com.linebacker.backend.sendmessage;

/**
 * Service definition for Sendmessage (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link SendmessageRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Sendmessage extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.22.0 of the sendmessage library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://linebacker-backend.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "sendmessage/v1/sendMessage/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Sendmessage(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Sendmessage(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * An accessor for creating requests from the SendMessageEndpoint collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Sendmessage sendmessage = new Sendmessage(...);}
   *   {@code Sendmessage.SendMessageEndpoint.List request = sendmessage.sendMessageEndpoint().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public SendMessageEndpoint sendMessageEndpoint() {
    return new SendMessageEndpoint();
  }

  /**
   * The "sendMessageEndpoint" collection of methods.
   */
  public class SendMessageEndpoint {

    /**
     * Create a request for the method "sendMessageEndpoint.sendMessage".
     *
     * This request holds the parameters needed by the sendmessage server.  After setting any optional
     * parameters, call the {@link SendMessage#execute()} method to invoke the remote operation.
     *
     * @param message
     * @param deviceId
     * @return the request
     */
    public SendMessage sendMessage(java.lang.String message, java.lang.String deviceId) throws java.io.IOException {
      SendMessage result = new SendMessage(message, deviceId);
      initialize(result);
      return result;
    }

    public class SendMessage extends SendmessageRequest<Void> {

      private static final String REST_PATH = "{message}/{deviceId}";

      /**
       * Create a request for the method "sendMessageEndpoint.sendMessage".
       *
       * This request holds the parameters needed by the the sendmessage server.  After setting any
       * optional parameters, call the {@link SendMessage#execute()} method to invoke the remote
       * operation. <p> {@link
       * SendMessage#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
       * must be called to initialize this instance immediately after invoking the constructor. </p>
       *
       * @param message
       * @param deviceId
       * @since 1.13
       */
      protected SendMessage(java.lang.String message, java.lang.String deviceId) {
        super(Sendmessage.this, "POST", REST_PATH, null, Void.class);
        this.message = com.google.api.client.util.Preconditions.checkNotNull(message, "Required parameter message must be specified.");
        this.deviceId = com.google.api.client.util.Preconditions.checkNotNull(deviceId, "Required parameter deviceId must be specified.");
      }

      @Override
      public SendMessage setAlt(java.lang.String alt) {
        return (SendMessage) super.setAlt(alt);
      }

      @Override
      public SendMessage setFields(java.lang.String fields) {
        return (SendMessage) super.setFields(fields);
      }

      @Override
      public SendMessage setKey(java.lang.String key) {
        return (SendMessage) super.setKey(key);
      }

      @Override
      public SendMessage setOauthToken(java.lang.String oauthToken) {
        return (SendMessage) super.setOauthToken(oauthToken);
      }

      @Override
      public SendMessage setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (SendMessage) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public SendMessage setQuotaUser(java.lang.String quotaUser) {
        return (SendMessage) super.setQuotaUser(quotaUser);
      }

      @Override
      public SendMessage setUserIp(java.lang.String userIp) {
        return (SendMessage) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.String message;

      /**

       */
      public java.lang.String getMessage() {
        return message;
      }

      public SendMessage setMessage(java.lang.String message) {
        this.message = message;
        return this;
      }

      @com.google.api.client.util.Key
      private java.lang.String deviceId;

      /**

       */
      public java.lang.String getDeviceId() {
        return deviceId;
      }

      public SendMessage setDeviceId(java.lang.String deviceId) {
        this.deviceId = deviceId;
        return this;
      }

      @Override
      public SendMessage set(String parameterName, Object value) {
        return (SendMessage) super.set(parameterName, value);
      }
    }

  }

  /**
   * Builder for {@link Sendmessage}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Sendmessage}. */
    @Override
    public Sendmessage build() {
      return new Sendmessage(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link SendmessageRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setSendmessageRequestInitializer(
        SendmessageRequestInitializer sendmessageRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(sendmessageRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
