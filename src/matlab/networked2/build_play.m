function build_play(inMode)
import edu.gatech.grits.puppetctrl.mdl.util.*;

v = 0.05;
model = @motor;
dmodelda = @motor_dalpha;

for i=1: length(inMode)
    i
    NewMode.v = v;
    Action = inMode{i}.getAction.getClass.getSimpleName;
    filename = lower(char(Action));
    handle = str2func(filename);
    NewMode.ctrl = @(x, t, a, td) handle(x, t, a, model, @(x,g) planar_motion(x, 0, g, a), td);
    NewMode.d_ctrl = @(x, t, a, td) handle(x, t, a, dmodelda, @(x,g) dplanar_motion(x, v, g, a), td);
    NewMode.alpha = inMode{i}.getScale;
    NewMode.tau = inMode{i}.getTimeLength;
    
    [start_idx, end_idx, extendts, matches] = regexp(char(inMode{i}.getRegion), '\d');
    NewMode.region = str2num(matches{1});
    
    PLAY{i} = NewMode;
end

PLAY

end
