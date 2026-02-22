double score(double * input) {
    double var0;
    if (input[1] <= 94.5) {
        if (input[2] <= 95.31499290466309) {
            if (input[3] <= 257.5) {
                var0 = 0.8108108108108109;
            } else {
                var0 = 1.0;
            }
        } else {
            var0 = 0.0;
        }
    } else {
        if (input[2] <= 9.559731006622314) {
            if (input[3] <= 396.5) {
                var0 = 0.9975845410628019;
            } else {
                var0 = 0.16666666666666666;
            }
        } else {
            if (input[0] <= 22.5) {
                var0 = 1.0;
            } else {
                var0 = 0.14944607732308388;
            }
        }
    }
    double var1;
    if (input[1] <= 94.5) {
        if (input[2] <= 95.37256336212158) {
            if (input[3] <= 258.0) {
                var1 = 0.8;
            } else {
                var1 = 1.0;
            }
        } else {
            var1 = 0.0;
        }
    } else {
        if (input[2] <= 9.672277450561523) {
            if (input[3] <= 401.5) {
                var1 = 0.9915708812260536;
            } else {
                var1 = 0.28;
            }
        } else {
            if (input[0] <= 22.5) {
                var1 = 1.0;
            } else {
                var1 = 0.14866727941176472;
            }
        }
    }
    return (var0 + var1) * 0.5;
}
