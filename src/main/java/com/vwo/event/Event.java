package com.vwo.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.vwo.UUIDType5;
import com.vwo.logger.LoggerManager;

import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Event {
    private Integer account_id;
    private Integer experiment_id;
    private String uId;
    private String u;
    private Integer combination;
    private Double random;
    private Integer goal_id;
    private long sId;
    private String ap;
    private String ed;
    private static final LoggerManager LOGGER = LoggerManager.getLogger(Event.class);


    private Event(Builder builder) {
        this.account_id = builder.account_id;
        this.experiment_id = builder.experiment_id;
        this.uId = builder.uId;
        this.u = builder.u;
        this.combination = builder.combination;
        this.random = builder.random;
        this.goal_id = builder.goal_id;
        this.sId = builder.sId;
        this.ap = builder.ap;
        this.ed = builder.ed;
    }


    public static class Builder {
        private Integer account_id;
        private Integer experiment_id;
        private String uId;
        private String u;
        private Integer combination;
        private Double random;
        private Integer goal_id;
        private long sId;
        private final UUID CONSTANT_NAMESPACE = UUIDType5.nameUUIDFromNamespaceAndString(UUIDType5.NAMESPACE_URL, "https://vwo.com");
        private String ap;
        private String ed;

        private Builder() {
        }

        public Builder withaccount_id(Integer account_id) {
            this.account_id = account_id;
            return this;
        }

        public Builder withexperiment_id(Integer experiment_id) {
            this.experiment_id = experiment_id;
            return this;
        }

        public Builder withuId(String uId) {
            this.uId = uId;
            return this;
        }

        public Builder withUuid(Integer account_id, String uId) {
            UUID account_uuid = UUIDType5.nameUUIDFromNamespaceAndString(CONSTANT_NAMESPACE, this.account_id.toString());
            UUID user_uuid = UUIDType5.nameUUIDFromNamespaceAndString(account_uuid, this.uId);
            this.u = user_uuid.toString().replace("-", "").toUpperCase();
            LOGGER.debug("Uuid generated for userId:{} and accountId:{} is {}", uId, account_id, u.toString());
            return this;
        }

        public Builder withVariation(Integer combination) {
            this.combination = combination;
            return this;

        }

        public Builder withRandom(Double random) {
            this.random = random;
            return this;
        }

        public Builder withsId(long sId) {
            this.sId = sId;
            return this;
        }

        public Builder withgoal_id(Integer goal_id) {
            this.goal_id = goal_id;
            return this;
        }

        public Builder withAp() {
            this.ap = "server";
            return this;
        }

        public Builder withEd() {
            this.ed = "{'p': 'server'}";
            return this;
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Event build() {
            return new Event(this);
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "account_id=" + account_id +
                ", experiment_id=" + experiment_id +
                ", uId='" + uId + '\'' +
                ", u='" + u + '\'' +
                ", combination=" + combination +
                ", random=" + random +
                ", goal_id='" + goal_id + '\'' +
                ", sId=" + sId +
                ", ap='" + ap + '\'' +
                ", ed='" + ed + '\'' +
                '}';
    }
}
