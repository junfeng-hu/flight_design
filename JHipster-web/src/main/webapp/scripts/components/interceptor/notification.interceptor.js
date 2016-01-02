 'use strict';

angular.module('flightadminApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-flightadminApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-flightadminApp-params')});
                }
                return response;
            }
        };
    });
