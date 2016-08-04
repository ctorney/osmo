import cv2
import numpy as np
from math import *
import matplotlib.pyplot as plt 
# 4k camera matrix
camera_matrix = np.array( [[  2467.726893, 0,  1936.02964], [0, 2473.06961, 1081.48243], [0, 0,1.0]])
dc = np.array( [ -1.53501973e-01,3.04457563e-01,8.83127622e-05,6.93998940e-04,-1.90560255e-01])

# 1080 camera matrix
camera_matrix = np.array([[1.18208317e+03,0, 9.52682573e+02],[0, 1.18047659e+03, 5.37914578e+02],  [ 0,0,1.0]])
dc = np.array([-0.15515428,  0.2575828,   0.00030817,  0.00119713, -0.21363664])

mnum=45

mnum=54
angle= 180-141.8
mnum=55
angle= 180-128
mnum=56
angle= 180-121.3
mnum=104
angle= 14#180-138
cap = cv2.VideoCapture('DJI_0'+str(mnum)+'.MP4')
#angle=0.01

ret, frame = cap.read()
gray =frame# cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
cv2.imwrite(str(mnum)+'PRE.jpg',gray)

gray2 = cv2.undistort(gray,camera_matrix,dc)

cv2.imwrite(str(mnum)+'PRE2.jpg',gray)
alpha_=angle
beta_ = 90
gamma_=90

alpha = (alpha_ - 90.)*pi/180;
beta = (beta_ - 90.)*pi/180;
gamma = (gamma_ - 90.)*pi/180;
f = 12447
dist = 800

sz = gray.shape
w = sz[1]
h= sz[0]
f=camera_matrix[0,0]
A1 = np.array([[1, 0, -w/2],[0,1, -h/2],[0, 0,0],[0, 0,1]])
#A1 = np.array([[f, 0, -camera_matrix[0,2]],[0,f, -camera_matrix[1,2]],[0, 0,0],[0, 0,1]])

RX = np.array([[1,0,0,0],[0, cos(alpha), -sin(alpha), 0],[0,sin(alpha), cos(alpha),0],[0,0,0,1]])
RY = np.eye(4)
RZ = np.eye(4)

        #Mat RY = (Mat_<double>(4, 4) << cos(beta), 0, -sin(beta), 0, 0, 1, 0, 0, sin(beta), 0,  cos(beta), 0,                0, 0,          0, 1); 

        #Mat RZ = (Mat_<double>(4, 4) << cos(gamma), -sin(gamma), 0, 0,  sin(gamma),  cos(gamma), 0, 0,                0,          0, 1, 0,0,0,0, 1); 


R = RX #* RY * RZ; 

        
T = np.array([[1, 0, 0, 0],[0, 1, 0, 0],[0,0, 1, dist],[0, 0, 0, 1]])
A2 = np.array([[f, 0, w/2,0],[0, f, h/2, 0], [0,0,1,0]])

A2[0:3,0:3]=camera_matrix
      
M2 = np.dot(A2,np.dot(T, np.dot(R,A1)))
print(M2)
warped = cv2.warpPerspective(gray2, M2, (sz[1],sz[0]),flags=cv2.WARP_INVERSE_MAP)


cv2.imwrite(str(mnum)+'POST.jpg',warped)
cap.release()

_,M3 = cv2.invert(M2)


ymin =  ceil(-M3[2,2]/ M3[2,1])

yyy = np.arange(ymin,1000)

out  = (M3[2,1]*yyy + M3[2,2])
#plt.plot(yyy,out)

# this is how to convert x-y positions in the image to real 2-d coordinates
original = np.array([((0,800), (1920,800),(0,1000),(1920, 1000))], dtype=np.float32)
original = np.array([((0,800),(0,0))], dtype=np.float32)
converted = cv2.perspectiveTransform(original, M3)
print(converted)
plt.plot(converted[0,:,0],converted[0,:,1],'.')

img = gray2
ind = 0
map_x = np.zeros(img.shape[:2],np.float32)
map_y = np.zeros(img.shape[:2],np.float32)
rows,cols = img.shape[:2]


# this is a manual implementation of warpPerspective using perspectiveTransform on a 
# pixel by pixel basis so that new image is same size as old image
rescale=8
for j in range(rows):
    for i in range(cols):
        
        irs = rescale*( i - cols*0.5 ) + cols*0.5
        jrs = rescale*( j - rows) + rows
        original = np.array([((irs,jrs),(0,0))], dtype=np.float32)
        converted = cv2.perspectiveTransform(original, M2)

        if 0< converted[0,0,0] <cols and 0<  converted[0,0,1] <rows:
            map_x.itemset((j,i),converted[0,0,0])
            map_y.itemset((j,i),converted[0,0,1])
        else:     # Other pixel values set to zero
            map_x.itemset((j,i),0)
            map_y.itemset((j,i),0)

        

dst = cv2.remap(img,map_x,map_y,cv2.INTER_LINEAR)
cv2.imwrite(str(mnum)+'neess.jpg',dst)
