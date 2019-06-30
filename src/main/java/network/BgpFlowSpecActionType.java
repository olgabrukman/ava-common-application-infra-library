package network;

public enum BgpFlowSpecActionType {
    BLOCK,
    RATE_LIMIT,
    SET_DSCP,
    REDIRECT_TO_NEXTHOP,
    REDIRECT_TO_VRF
}
