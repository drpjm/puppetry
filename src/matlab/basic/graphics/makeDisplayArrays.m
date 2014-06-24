function [X,Y,Z] = makeDisplayArrays(data, index, side, res, limb)
% Function that creates three separate display arrays at an instant in
% time, k, and stores them in X,Y,Z.
% data - data to be turned into display arrays
% index - time index of data to use
% width - puppet shoulder width
% res - resolution of line
% limb - leg(1), arm(0) 

curr_x = data(1,index);
curr_y = data(2,index);
curr_z = data(3,index);

shoulder_width = 0.07;
hip_width = 0.06;

z0 = 0;
if(limb)
    width = hip_width;
    z0 = -0.08;
else
    width = shoulder_width;
end

if(side == 1) %if left
    y0 = width/2;
else
    y0 = -width/2;
end

if(curr_x == 0)
    X = zeros(1,res+1);
else
    X = 0:curr_x/res:curr_x;
end

if(curr_y - y0 == 0)
    Y = curr_y * ones(1,res+1);
else
    Y = y0 : (curr_y-y0)/res : curr_y;
end

if(curr_z - z0 == 0)
    Z = zeros(1,res+1);
else
    Z = z0:(curr_z - z0)/res:curr_z;
end
