% puppet play

model = @motor;
dmodelda = @motor_dalpha;

v = 0.05;

Mode1.ctrl = @(x, t, a, td) waveRight(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
Mode1.d_ctrl = @(x, t, a, td) waveRight(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
Mode1.v = v;
Mode1.alpha = 0.9;
Mode1.tau = 13;
Mode1.region = 1;

Mode2.ctrl = @(x, t, a,td) walk(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
Mode2.d_ctrl = @(x,t,a,td) walk(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a),td);
Mode2.v = v;
Mode2.alpha = 1;
Mode2.tau = 3;
Mode2.region = 1;

Mode3.ctrl = @(x, t, a,td) run(x, t, a, model, @(x,g) planar_motion(x, 0, g, a), td);
Mode3.d_ctrl = @(x, t, a,td) run(x, t, a, dmodelda, @(x,g) dplanar_motion(x, 0, g, a), td);
Mode3.v = 0;
Mode3.alpha = 1.2;
Mode3.tau = 3;
Mode3.region = 1;

Mode4.ctrl = @(x, t, a, td) waveLeft(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
Mode4.d_ctrl = @(x, t, a, td) waveLeft(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
Mode4.v = v;
Mode4.alpha = 0.9;
Mode4.tau = 13;
Mode4.region = 1;


%PLAY{1} = { Mode3 Mode1 Mode2 };
PLAY{1} = { Mode2 Mode3};


Mode4.ctrl = @(x, t, a,td) clockwise(x, t, a, model,  @(x,g) planar_motion(x, v, g, a), td);
Mode4.d_ctrl = @(x,t,a,td) clockwise(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
Mode4.v = v;
Mode4.alpha = 1.5;
Mode4.tau = 3;
Mode4.region = 3;

Mode5.ctrl = @(x, t, a, td) walk(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
Mode5.d_ctrl = @(x, t, a, td) walk(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
Mode5.v = v;
Mode5.alpha = 1.3;
Mode5.tau = 2.5;
Mode5.region = 3;

Mode6.ctrl = @(x, t, a, td) waveLeft(x, t, a, model, @(x,g) planar_motion(x, 0, g, a), td);
Mode6.d_ctrl = @(x, t, a, td) waveLeft(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
Mode6.v = 0;
Mode6.alpha = 1.2;
Mode6.tau = 2.5;
Mode6.region = 3;


PLAY{2} = { Mode6 Mode4 Mode5 };

% PUPPET 3'S SCRIPT

mode7.ctrl = @(x, t, a,td) counter_clockwise(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
mode7.d_ctrl = @(x,t,a,td) counter_clockwise(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
mode7.v = v;
mode7.alpha = 1;
mode7.tau = 4;
mode7.region = 2;

mode8.ctrl = @(x, t, a, td) walk(x, t, a, model, @(x,g) planar_motion(x, v, g, a), td);
mode8.d_ctrl = @(x, t, a, td) walk(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
mode8.v = v;
mode8.alpha = 1;
mode8.tau = 5;
mode8.region = 2;

mode9.ctrl = @(x, t, a, td) waveLeft(x, t, a, model, mode9.v, mode9.gamma);
mode9.d_ctrl = @(x, t, a, td) waveLeft(x, t, a, dmodelda, mode9.v, mode9.gamma);
mode9.v = 0;
mode9.alpha = 1.2;
mode9.tau = 2.5;
mode9.region = 2;

% PLAY{3} = { mode7 mode8 };