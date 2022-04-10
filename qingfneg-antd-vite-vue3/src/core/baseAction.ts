import store from '@/store'

export function findDataAuth(record) {
    const roles = store.getters.roles;
    const authOrganize = roles.authOrganize;
    let bol = false;
    let authOrgIds = authOrganize.authOrgIds;
    let authParams = authOrganize.authParams;
    let user_id = authOrganize.user_id;
    var createParams = record.create_organize + ":Y";
    if (authOrgIds == "" || authOrgIds == undefined) {
        if (user_id == record.create_user) {
            bol = true;
        }
    } else {
        if (
            authParams.indexOf(createParams) > -1 ||
            user_id == record.create_user
        ) {
            bol = true;
        }
    }
    return bol;
}
