'use strict';

angular.module('flightadminApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


