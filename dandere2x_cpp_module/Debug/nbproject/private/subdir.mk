################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../nbproject/private/cpp_standard_headers_indexer.cpp 

C_SRCS += \
../nbproject/private/c_standard_headers_indexer.c 

OBJS += \
./nbproject/private/c_standard_headers_indexer.o \
./nbproject/private/cpp_standard_headers_indexer.o 

CPP_DEPS += \
./nbproject/private/cpp_standard_headers_indexer.d 

C_DEPS += \
./nbproject/private/c_standard_headers_indexer.d 


# Each subdirectory must supply rules for building sources it contributes
nbproject/private/%.o: ../nbproject/private/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

nbproject/private/%.o: ../nbproject/private/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -O3 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


