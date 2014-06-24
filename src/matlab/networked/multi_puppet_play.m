% puppet play

% PUPPET 2'S SCRIPT
model = @motor;
d_model = @motor_dalpha;

mode1.v = 0.05;
mode1.gamma = 0;
mode1.ctrl = @(x, t, a) walk(x, t, a, model, mode1.v, mode1.gamma);
mode1.d_ctrl = @(x, t, a) walk(x, t, a, d_model, mode1.v, mode1.gamma);
mode1.alpha = 1.3;
mode1.tau = 3;
mode1.region = 1;
% mode1.P = cost_matrix(1);

mode2.v = 0.05;
mode2.gamma = 1;
mode2.ctrl = @(x, t, a) walkInCircles(x, t, a, model, mode2.v, mode2.gamma);
mode2.d_ctrl = @(x,t,a) walkInCircles(x, t, a, d_model, mode2.v, mode2.gamma);
mode2.alpha = 1;
mode2.tau = 4;
mode2.region = 1;
% mode2.P = cost_matrix(1);

mode3.v = 0;
mode3.gamma = 0;
mode3.ctrl = @(x, t, a) waveLeft(x, t, a, model, mode3.v, mode3.gamma);
mode3.d_ctrl = @(x, t, a) waveLeft(x, t, a, d_model, mode3.v, mode3.gamma);
mode3.alpha = 1.2;
mode3.tau = 2.5;
mode3.region = 1;
% mode3.P = cost_matrix(0);


PLAY{1} = { mode3 mode1 mode2 };

% PUPPET 2'S SCRIPT
mode4.v = 0.05;
mode4.gamma = 1;
mode4.ctrl = @(x, t, a) walkInCircles(x, t, a, model, mode4.v, mode4.gamma);
mode4.d_ctrl = @(x,t,a) walkInCircles(x, t, a, d_model, mode4.v, mode4.gamma);
mode4.alpha = 1.5;
mode4.tau = 2;
mode4.region = 3;
% mode4.P = cost_matrix(1);

mode5.v = 0.05;
mode5.gamma = 0;
mode5.ctrl = @(x, t, a) walk(x, t, a, model, mode5.v, mode5.gamma);
mode5.d_ctrl = @(x, t, a) walk(x, t, a, d_model, mode5.v, mode5.gamma);
mode5.alpha = 1.3;
mode5.tau = 3;
mode5.region = 3;
% mode5.P = cost_matrix(1);

mode6.v = 0;
mode6.gamma = 0;
mode6.ctrl = @(x, t, a) waveLeft(x, t, a, model, mode6.v, mode6.gamma);
mode6.d_ctrl = @(x, t, a) waveLeft(x, t, a, d_model, mode6.v, mode6.gamma);
mode6.alpha = 1.2;
mode6.tau = 2.5;
mode6.region = 3;
% mode6.P = cost_matrix(0);

PLAY{2} = { mode6 mode4 mode5 };

% PUPPET 3'S SCRIPT

mode7.v = 0.05;
mode7.gamma = -1;
mode7.ctrl = @(x, t, a) walkInCircles(x, t, a, model, mode7.v, mode7.gamma);
mode7.d_ctrl = @(x,t,a) walkInCircles(x, t, a, d_model, mode7.v, mode7.gamma);
mode7.alpha = 1;
mode7.tau = 2;
mode7.region = 2;

mode8.v = 0.05;
mode8.gamma = 0;
mode8.ctrl = @(x, t, a) walk(x, t, a, model, mode8.v, mode8.gamma);
mode8.d_ctrl = @(x, t, a) walk(x, t, a, d_model, mode8.v, mode8.gamma);
mode8.alpha = 1.5;
mode8.tau = 4;
mode8.region = 2;

mode9.v = 0;
mode9.gamma = 0;
mode9.ctrl = @(x, t, a) waveLeft(x, t, a, model, mode9.v, mode9.gamma);
mode9.d_ctrl = @(x, t, a) waveLeft(x, t, a, d_model, mode9.v, mode9.gamma);
mode9.alpha = 1.2;
mode9.tau = 2.5;
mode9.region = 2;

PLAY{3} = { mode7 mode8 };
