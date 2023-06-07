angular.module('demo', [])
    .controller('Hello', function($scope, $http) {
        $http.get('https://rest-service.guides.spring.io/greeting').
        then(function(response) {
            $scope.greeting = response.data;
        });
    });